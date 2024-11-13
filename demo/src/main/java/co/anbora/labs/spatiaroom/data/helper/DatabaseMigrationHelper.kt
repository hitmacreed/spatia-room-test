package co.anbora.labs.spatiaroom.data.helper
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseMigrationHelper {

    fun initializeSpatialMetaData(db: SupportSQLiteDatabase) {
        db.query("SELECT InitSpatialMetaData();").moveToNext()
    }

    fun createNewAddressesTable(db: SupportSQLiteDatabase) {
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
    }

    fun createSpatialIndex(db: SupportSQLiteDatabase) {
        db.query("""
            SELECT CreateSpatialIndex(
                'pt_addresses_2_new', 
                'Geometry'
            );
        """).moveToNext()
    }

    fun migrateDataFromOriginalTable(db: SupportSQLiteDatabase) {
        db.execSQL("""
            INSERT INTO "pt_addresses_2_new" 
            SELECT "id", "country", "city", "postcode", "street", "housename", "housenumber", "Geometry" 
            FROM "pt_addresses"
        """)
    }

    fun dropOldTables(db: SupportSQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS `pt_addresses`")
        db.execSQL("DROP TABLE IF EXISTS `pt_addresses_cp`")
    }

    fun renameNewTable(db: SupportSQLiteDatabase) {
        db.execSQL("""
            ALTER TABLE "pt_addresses_2_new" 
            RENAME TO "pt_addresses_cp"
        """)
    }
}
