package com.ferrine.antridong.database;

public final class DbMetadata {
    private DbMetadata() {}

    // Table: User
    public static final class User {
        public static final String TABLE_NAME = "users";
        public static final String COL_ID = "id";
        public static final String COL_USERNAME = "username";
        public static final String COL_PASSWORD = "password";
        public static final String COL_NAME = "name";
        public static final String COL_ROLE = "role";
    }

    // Table: KategoriAntrian
    public static final class KategoriAntrian {
        public static final String TABLE_NAME = "kategori_antrian";
        public static final String COL_ID = "id";
        public static final String COL_CODE = "code"; // 3 characters unique code
        public static final String COL_NAME = "name";
        public static final String COL_START_TIME = "start_time"; // Valid hours range start
        public static final String COL_END_TIME = "end_time"; // Valid hours range end
    }

    // Table: Counter
    public static final class Counter {
        public static final String TABLE_NAME = "counters";
        public static final String COL_ID = "id";
        public static final String COL_NAME = "name"; // e.g. "Meja 1"
        public static final String COL_STATUS = "status"; // active / inactive
    }

    // Join Table: Counter - KategoriAntrian (Many-to-Many relation)
    public static final class CounterKategori {
        public static final String TABLE_NAME = "counter_kategori";
        public static final String COL_COUNTER_ID = "counter_id";
        public static final String COL_KATEGORI_ID = "kategori_id";
    }

    // Table: Antrian (QueueTicket)
    public static final class Antrian {
        public static final String TABLE_NAME = "antrian";
        public static final String COL_ID = "id";
        public static final String COL_TICKET_NUMBER = "ticket_number"; // e.g. "ADM001"
        public static final String COL_VISITOR_NAME = "visitor_name";
        public static final String COL_STATUS = "status"; // waiting, calling, completed, skipped
        public static final String COL_CREATED_AT = "created_at";
        public static final String COL_CALLED_AT = "called_at";
        
        public static final String COL_KATEGORI_ID = "kategori_id";
        public static final String COL_COUNTER_ID = "counter_id";
    }
}
