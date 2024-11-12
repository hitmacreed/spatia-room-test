package co.anbora.labs.spatiaroom.data.helper
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseMigrationHelper {

    fun initializeSpatialMetaData(db: SupportSQLiteDatabase) {
        db.query("SELECT InitSpatialMetaData();").moveToNext()
    }

    fun createNewAmenityTable(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE "amenity_internet_cafe_lisboa_2_new" (
                "ogc_fid" INTEGER, 
                "full_id" TEXT, 
                "osm_id" TEXT, 
                "osm_type" TEXT, 
                "amenity" TEXT, 
                "wheelchair" TEXT, 
                "opening_hours" TEXT, 
                "name" TEXT, 
                "internet_access" TEXT, 
                "GEOMETRY" BLOB, 
                PRIMARY KEY("ogc_fid")
            )
        """)
    }

    fun addGeometryColumn(db: SupportSQLiteDatabase) {
        db.query("""
            SELECT AddGeometryColumn(
                'amenity_internet_cafe_lisboa_2_new', 
                'GEOMETRY', 
                4326, 
                'POINT', 
                'XY'
            );
        """).moveToNext()
    }

    fun createSpatialIndex(db: SupportSQLiteDatabase) {
        db.query("""
            SELECT CreateSpatialIndex(
                'amenity_internet_cafe_lisboa_2_new', 
                'GEOMETRY'
            );
        """).moveToNext()
    }

    fun migrateDataFromOldTable(db: SupportSQLiteDatabase) {
        db.execSQL("""
            INSERT INTO "amenity_internet_cafe_lisboa_2_new" 
            SELECT "ogc_fid", "full_id", "osm_id", "osm_type", "amenity", "wheelchair", 
                   "opening_hours", "name", "internet_access", "GEOMETRY" 
            FROM "amenity_internet_cafe_lisboa"
        """)
    }

    fun dropOldTables(db: SupportSQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS `amenity_internet_cafe_lisboa`")
        db.execSQL("DROP TABLE IF EXISTS `amenity_internet_cafe_lisboa_2`")
    }

    fun renameNewTable(db: SupportSQLiteDatabase) {
        db.execSQL("""
            ALTER TABLE "amenity_internet_cafe_lisboa_2_new" 
            RENAME TO "amenity_internet_cafe_lisboa_2"
        """)
    }
}
