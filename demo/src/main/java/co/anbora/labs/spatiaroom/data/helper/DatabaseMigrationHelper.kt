package co.anbora.labs.spatiaroom.data.helper
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseMigrationHelper {

    // databaseTable_temp = temporary table
    // databaseTable_cp = copy of original table

    fun initializeSpatialMetaData(db: SupportSQLiteDatabase) {
        db.query("SELECT InitSpatialMetaData();").moveToNext()
    }

    fun createNewTables(db: SupportSQLiteDatabase) {

        ////////// ADDRESS TABLE //////////////
        db.execSQL("""
            CREATE TABLE "pt_addresses_temp" (
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

        ////////// AMENITY TABLE //////////////
        db.execSQL("""
            CREATE TABLE "pt_amenity_temp" (
                "id" INTEGER, 
                "sub_type" TEXT, 
                "name" TEXT, 
                "Geometry" BLOB, 
                PRIMARY KEY("id")
            )
        """)
    }

    fun addGeometryColumn(db: SupportSQLiteDatabase) {
        ////////// ADDRESS TABLE //////////////
        db.query("""
            SELECT AddGeometryColumn(
                'pt_addresses_temp', 
                'Geometry', 
                4326, 
                'POINT', 
                'XY'
            );
        """).moveToNext()

        ////////// AMENITY TABLE //////////////
        db.query("""
            SELECT AddGeometryColumn(
                'pt_amenity_temp', 
                'Geometry', 
                4326, 
                'POINT', 
                'XY'
            );
        """).moveToNext()
    }

    fun createSpatialIndex(db: SupportSQLiteDatabase) {
        ////////// ADDRESS TABLE //////////////
        db.query("""
            SELECT CreateSpatialIndex(
                'pt_addresses_temp', 
                'Geometry'
            );
        """)

        ////////// AMENITY TABLE //////////////
        db.query("""
            SELECT CreateSpatialIndex(
                'pt_amenity_temp', 
                'Geometry'
            );
        """)
    }

    fun migrateDataFromOriginalTable(db: SupportSQLiteDatabase) {
        ////////// ADDRESS TABLE //////////////
        db.execSQL("""
            INSERT INTO "pt_addresses_temp" 
            SELECT "id", "country", "city", "postcode", "street", "housename", "housenumber", "Geometry" 
            FROM "pt_addresses"
        """)

        ////////// AMENITY TABLE //////////////
        db.execSQL("""
            INSERT INTO "pt_amenity_temp" 
            SELECT "id", "sub_type", "name", "Geometry" 
            FROM "pt_amenity"
        """)
    }

    fun dropOldTables(db: SupportSQLiteDatabase) {
        ////////// ADDRESS TABLE //////////////
        db.execSQL("DROP TABLE IF EXISTS `pt_addresses`")
        db.execSQL("DROP TABLE IF EXISTS `pt_addresses_cp`")

        ////////// AMENITY TABLE //////////////
        db.execSQL("DROP TABLE IF EXISTS `pt_amenity`")
        db.execSQL("DROP TABLE IF EXISTS `pt_amenity_cp`")
    }

    fun renameNewTable(db: SupportSQLiteDatabase) {
        ////////// ADDRESS TABLE //////////////
        db.execSQL("""
            ALTER TABLE "pt_addresses_temp" 
            RENAME TO "pt_addresses_cp"
        """)

        ////////// AMENITY TABLE //////////////
        db.execSQL("""
            ALTER TABLE "pt_amenity_temp" 
            RENAME TO "pt_amenity_cp"
        """)
    }
}
