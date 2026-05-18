package com.ferrine.antridong.controller;

import com.ferrine.antridong.database.DbMetadata;
import com.ferrine.antridong.database.models.Antrian;
import com.ferrine.antridong.database.models.Counter;
import com.ferrine.antridong.database.models.KategoriAntrian;
import com.ferrine.antridong.database.repository.AntrianRepository;
import com.ferrine.antridong.database.repository.CounterRepository;
import com.ferrine.antridong.database.repository.KategoriAntrianRepository;
import com.ferrine.antridong.service.QueueStateService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class PageController {

    private final CounterRepository counterRepository;
    private final KategoriAntrianRepository kategoriRepository;
    private final AntrianRepository antrianRepository;
    private final QueueStateService queueStateService;
    private final SimpMessagingTemplate messagingTemplate;

    public PageController(CounterRepository counterRepository,
                          KategoriAntrianRepository kategoriRepository,
                          AntrianRepository antrianRepository,
                          QueueStateService queueStateService,
                          SimpMessagingTemplate messagingTemplate) {
        this.counterRepository = counterRepository;
        this.kategoriRepository = kategoriRepository;
        this.antrianRepository = antrianRepository;
        this.queueStateService = queueStateService;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping("/")
    public String index(Model model) {
        List<Counter> counters = counterRepository.query()
                .orderBy(DbMetadata.Counter.COL_NAME + " asc")
                .findList();
        model.addAttribute("counters", counters);
        return "index";
    }

    @GetMapping("/dashboard/antrian")
    public String antrianDashboard(Model model) {
        // State Restoration: Ambil antrian terakhir yang dipanggil
        Antrian lastCalled = queueStateService.getLastCalledQueue();
        model.addAttribute("lastCalled", lastCalled);

        // Ambil daftar counter beserta antrian terakhir yang berstatus CALLED untuk masing-masing counter
        List<Counter> counters = counterRepository.query()
                .orderBy(DbMetadata.Counter.COL_NAME + " asc")
                .findList();

        Map<Long, Antrian> currentServingMap = new HashMap<>();
        for (Counter counter : counters) {
            Antrian serving = antrianRepository.query()
                    .where()
                    .eq(DbMetadata.Antrian.COL_COUNTER_ID, counter.getId())
                    .eq(DbMetadata.Antrian.COL_STATUS, "CALLED")
                    .orderBy(DbMetadata.Antrian.COL_CALLED_AT + " desc")
                    .setMaxRows(1)
                    .findOne();
            if (serving != null) {
                currentServingMap.put(counter.getId(), serving);
            }
        }

        model.addAttribute("counters", counters);
        model.addAttribute("servingMap", currentServingMap);
        return "dashboard-antrian";
    }

    @GetMapping("/dashboard/pengunjung")
    public String pengunjungDashboard(Model model) {
        List<KategoriAntrian> kategoriList = kategoriRepository.query()
                .orderBy(DbMetadata.KategoriAntrian.COL_CODE + " asc")
                .findList();

        // Evaluasi jam valid kategori berdasarkan jam server saat ini
        LocalTime now = LocalTime.now();
        Map<Long, Boolean> activeMap = new HashMap<>();
        for (KategoriAntrian kat : kategoriList) {
            boolean isValid = !now.isBefore(kat.getStartTime()) && !now.isAfter(kat.getEndTime());
            activeMap.put(kat.getId(), isValid);
        }

        model.addAttribute("kategoriList", kategoriList);
        model.addAttribute("activeMap", activeMap);
        return "dashboard-pengunjung";
    }

    @PostMapping("/dashboard/pengunjung/register")
    @ResponseBody
    public ResponseEntity<?> registerTicket(@RequestParam("kategoriId") Long kategoriId,
                                            @RequestParam(value = "visitorName", required = false) String visitorName) {
        KategoriAntrian category = kategoriRepository.query()
                .where()
                .eq(DbMetadata.KategoriAntrian.COL_ID, kategoriId)
                .findOne();

        if (category == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Kategori tidak ditemukan"));
        }

        // Validasi jam valid kategori
        LocalTime nowTime = LocalTime.now();
        if (nowTime.isBefore(category.getStartTime()) || nowTime.isAfter(category.getEndTime())) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Kategori berada di luar jam layanan"));
        }

        // Generate nomor antrian baru (Sequence per kategori hari ini)
        Antrian lastTicket = antrianRepository.query()
                .where()
                .eq(DbMetadata.Antrian.COL_KATEGORI_ID, category.getId())
                .ge(DbMetadata.Antrian.COL_CREATED_AT, LocalDateTime.now().with(LocalTime.MIN))
                .orderBy(DbMetadata.Antrian.COL_CREATED_AT + " desc")
                .setMaxRows(1)
                .findOne();

        int nextSeq = 1;
        if (lastTicket != null) {
            String lastNumStr = lastTicket.getTicketNumber();
            if (lastNumStr.length() > 3) {
                String numStr = lastNumStr.substring(3);
                try {
                    nextSeq = Integer.parseInt(numStr) + 1;
                } catch (NumberFormatException e) {
                    // Fallback
                }
            }
        }
        String ticketNumber = String.format("%s%03d", category.getCode(), nextSeq);

        // Simpan data antrian baru
        Antrian ticket = new Antrian();
        ticket.setTicketNumber(ticketNumber);
        ticket.setVisitorName(visitorName != null && !visitorName.trim().isEmpty() ? visitorName.trim() : "Umum");
        ticket.setStatus("PENDING");
        ticket.setKategori(category);
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.save();

        // Broadcast event ticket baru ditambahkan
        messagingTemplate.convertAndSend("/topic/queue", Map.of(
                "event", "TICKET_ADDED",
                "kategoriId", category.getId()
        ));

        return ResponseEntity.ok(Map.of(
                "success", true,
                "ticketNumber", ticketNumber,
                "visitorName", ticket.getVisitorName(),
                "kategoriName", category.getName(),
                "createdAt", ticket.getCreatedAt().toString()
        ));
    }

    @GetMapping("/dashboard/counter/{id}")
    public String counterDashboard(@PathVariable("id") Long id, Model model) {
        Counter counter = counterRepository.query()
                .where()
                .eq(DbMetadata.Counter.COL_ID, id)
                .findOne();

        if (counter == null) {
            return "redirect:/";
        }

        // State Restoration: Ambil antrian terakhir yang dipanggil oleh counter ini
        Antrian currentTicket = antrianRepository.query()
                .where()
                .eq(DbMetadata.Antrian.COL_COUNTER_ID, counter.getId())
                .eq(DbMetadata.Antrian.COL_STATUS, "CALLED")
                .orderBy(DbMetadata.Antrian.COL_CALLED_AT + " desc")
                .setMaxRows(1)
                .findOne();

        // Ambil daftar antrian PENDING untuk kategori counter ini (FIFO)
        List<Long> kategoriIds = counter.getKategoriList().stream().map(KategoriAntrian::getId).collect(Collectors.toList());
        List<Antrian> pendingQueues = new ArrayList<>();
        if (!kategoriIds.isEmpty()) {
            pendingQueues = antrianRepository.query()
                    .where()
                    .eq(DbMetadata.Antrian.COL_STATUS, "PENDING")
                    .in(DbMetadata.Antrian.COL_KATEGORI_ID, kategoriIds)
                    .orderBy(DbMetadata.Antrian.COL_CREATED_AT + " asc")
                    .findList();
        }

        model.addAttribute("counter", counter);
        model.addAttribute("currentTicket", currentTicket);
        model.addAttribute("pendingQueues", pendingQueues);
        return "dashboard-counter";
    }

    @GetMapping("/dashboard/counter/{id}/queues")
    @ResponseBody
    public ResponseEntity<?> getCounterQueues(@PathVariable("id") Long id) {
        Counter counter = counterRepository.query()
                .where()
                .eq(DbMetadata.Counter.COL_ID, id)
                .findOne();

        if (counter == null) {
            return ResponseEntity.notFound().build();
        }

        List<Long> kategoriIds = counter.getKategoriList().stream().map(KategoriAntrian::getId).collect(Collectors.toList());
        List<Antrian> pendingQueues = new ArrayList<>();
        if (!kategoriIds.isEmpty()) {
            pendingQueues = antrianRepository.query()
                    .where()
                    .eq(DbMetadata.Antrian.COL_STATUS, "PENDING")
                    .in(DbMetadata.Antrian.COL_KATEGORI_ID, kategoriIds)
                    .orderBy(DbMetadata.Antrian.COL_CREATED_AT + " asc")
                    .findList();
        }

        List<Map<String, Object>> list = pendingQueues.stream().map(q -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", q.getId());
            map.put("ticketNumber", q.getTicketNumber());
            map.put("visitorName", q.getVisitorName());
            map.put("kategoriName", q.getKategori().getName());
            map.put("createdAt", q.getCreatedAt().toString());
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(list);
    }

    @PostMapping("/dashboard/counter/{id}/toggle")
    @ResponseBody
    public ResponseEntity<?> toggleCounter(@PathVariable("id") Long id) {
        Counter counter = counterRepository.query()
                .where()
                .eq(DbMetadata.Counter.COL_ID, id)
                .findOne();

        if (counter == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Counter tidak ditemukan"));
        }

        String newStatus = "active".equals(counter.getStatus()) ? "inactive" : "active";
        counter.setStatus(newStatus);
        counter.save();

        // Broadcast status change
        messagingTemplate.convertAndSend("/topic/queue", Map.of(
                "event", "COUNTER_STATUS_CHANGED",
                "counterId", counter.getId(),
                "status", newStatus
        ));

        return ResponseEntity.ok(Map.of("success", true, "status", newStatus));
    }

    @PostMapping("/dashboard/counter/{id}/call")
    @ResponseBody
    public ResponseEntity<?> callNext(@PathVariable("id") Long id) {
        Counter counter = counterRepository.query()
                .where()
                .eq(DbMetadata.Counter.COL_ID, id)
                .findOne();

        if (counter == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Counter tidak ditemukan"));
        }

        if (!"active".equals(counter.getStatus())) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Counter tidak aktif. Silakan aktifkan counter terlebih dahulu."));
        }

        List<Long> kategoriIds = counter.getKategoriList().stream().map(KategoriAntrian::getId).collect(Collectors.toList());
        if (kategoriIds.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Counter ini tidak memiliki kategori antrian yang di-assign."));
        }

        Antrian nextTicket = antrianRepository.query()
                .where()
                .eq(DbMetadata.Antrian.COL_STATUS, "PENDING")
                .in(DbMetadata.Antrian.COL_KATEGORI_ID, kategoriIds)
                .orderBy(DbMetadata.Antrian.COL_CREATED_AT + " asc")
                .setMaxRows(1)
                .findOne();

        if (nextTicket == null) {
            return ResponseEntity.ok(Map.of("success", false, "message", "Antrian kosong."));
        }

        // Selesaikan tiket-tiket sebelumnya yang berstatus CALLED di counter ini
        List<Antrian> previouslyCalled = antrianRepository.query()
                .where()
                .eq(DbMetadata.Antrian.COL_COUNTER_ID, counter.getId())
                .eq(DbMetadata.Antrian.COL_STATUS, "CALLED")
                .findList();
        for (Antrian oldTicket : previouslyCalled) {
            oldTicket.setStatus("COMPLETED");
            oldTicket.save();
        }

        // Set status ke CALLED untuk tiket terpilih
        nextTicket.setStatus("CALLED");
        nextTicket.setCounter(counter);
        nextTicket.setCalledAt(LocalDateTime.now());
        nextTicket.save();

        // Broadcast pemanggilan
        messagingTemplate.convertAndSend("/topic/queue", Map.of(
                "event", "TICKET_CALLED",
                "ticketId", nextTicket.getId(),
                "ticketNumber", nextTicket.getTicketNumber(),
                "visitorName", nextTicket.getVisitorName(),
                "counterId", counter.getId(),
                "counterName", counter.getName()
        ));

        return ResponseEntity.ok(Map.of(
                "success", true,
                "ticketNumber", nextTicket.getTicketNumber(),
                "visitorName", nextTicket.getVisitorName()
        ));
    }

    @PostMapping("/dashboard/counter/{id}/recall")
    @ResponseBody
    public ResponseEntity<?> recallTicket(@PathVariable("id") Long id) {
        Counter counter = counterRepository.query()
                .where()
                .eq(DbMetadata.Counter.COL_ID, id)
                .findOne();

        if (counter == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Counter tidak ditemukan"));
        }

        // Ambil antrian CALLED terakhir di counter ini
        Antrian currentTicket = antrianRepository.query()
                .where()
                .eq(DbMetadata.Antrian.COL_COUNTER_ID, counter.getId())
                .eq(DbMetadata.Antrian.COL_STATUS, "CALLED")
                .orderBy(DbMetadata.Antrian.COL_CALLED_AT + " desc")
                .setMaxRows(1)
                .findOne();

        if (currentTicket == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Tidak ada antrian yang sedang dipanggil di counter ini."));
        }

        // Broadcast ulang pemanggilan
        messagingTemplate.convertAndSend("/topic/queue", Map.of(
                "event", "TICKET_CALLED",
                "ticketId", currentTicket.getId(),
                "ticketNumber", currentTicket.getTicketNumber(),
                "visitorName", currentTicket.getVisitorName(),
                "counterId", counter.getId(),
                "counterName", counter.getName()
        ));

        return ResponseEntity.ok(Map.of("success", true, "ticketNumber", currentTicket.getTicketNumber()));
    }
}
