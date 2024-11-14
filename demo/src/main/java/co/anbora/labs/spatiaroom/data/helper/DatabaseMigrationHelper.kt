package co.anbora.labs.spatiaroom.data.helper
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseMigrationHelper {

    fun initializeSpatialMetaData(db: SupportSQLiteDatabase) {
        db.query("SELECT InitSpatialMetaData();").moveToNext()
    }

    fun createNewTables(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE "pt_addresses_2_new" (
                "id" INTEGER, 
                "country" TEXT, 
                "city" TEXT, 
                "postcode" TEXT, 
                "street" TEXT, 
                "housename" TEXT, 
                "housenumber" TEXT, 
                "Geometry" BLOB, 
                PRIMARY KEY("id")
            )
        """)

        db.execSQL("""
            CREATE TABLE "pt_amenity_2_new" (
                "id" INTEGER, 
                "sub_type" TEXT, 
                "name" TEXT, 
                "Geometry" BLOB, 
                PRIMARY KEY("id")
            )
        """)
    }

    fun addGeometryColumn(db: SupportSQLiteDatabase) {
        db.query("""
            SELECT AddGeometryColumn(
                'pt_addresses_2_new', 
                'Geometry', 
                4326, 
                'POINT', 
                'XY'
            );
        """).moveToNext()


        db.query("""
            SELECT AddGeometryColumn(
                'pt_amenity_2_new', 
                'Geometry', 
                4326, 
                'POINT', 
                'XY'
            );
        """).moveToNext()
    }

    fun createSpatialIndex(db: SupportSQLiteDatabase) {
        db.query("""
            SELECT CreateSpatialIndex(
                'pt_addresses_2_new', 
                'Geometry'
            );
        """)

        db.query("""
            SELECT CreateSpatialIndex(
                'pt_amenity_2_new', 
                'Geometry'
            );
        """)
    }

    fun migrateDataFromOriginalTable(db: SupportSQLiteDatabase) {
        db.execSQL("""
            INSERT INTO "pt_addresses_2_new" 
            SELECT "id", "country", "city", "postcode", "street", "housename", "housenumber", "Geometry" 
            FROM "pt_addresses"
        """)

        db.execSQL("""
            INSERT INTO "pt_amenity_2_new" 
            SELECT "id", "sub_type", "name", "Geometry" 
            FROM "pt_amenity"
        """)
    }

    fun dropOldTables(db: SupportSQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS `pt_addresses`")
        db.execSQL("DROP TABLE IF EXISTS `pt_addresses_cp`")

        db.execSQL("DROP TABLE IF EXISTS `pt_amenity`")
        db.execSQL("DROP TABLE IF EXISTS `pt_amenity_cp`")
    }

    fun renameNewTable(db: SupportSQLiteDatabase) {
        db.execSQL("""
            ALTER TABLE "pt_addresses_2_new" 
            RENAME TO "pt_addresses_cp"
        """)

        db.execSQL("""
            ALTER TABLE "pt_amenity_2_new" 
            RENAME TO "pt_amenity_cp"
        """)
    }
}
