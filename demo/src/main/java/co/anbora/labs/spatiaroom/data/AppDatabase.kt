package co.anbora.labs.spatiaroom.data


import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import co.anbora.labs.spatia.builder.SpatiaRoom
import co.anbora.labs.spatia.geometry.GeometryConverters
import co.anbora.labs.spatiaroom.data.dao.AmenitiesDao
import co.anbora.labs.spatiaroom.data.model.PtAmenity
import co.anbora.labs.spatiaroom.data.model.PtAmenity.Companion.TABLE_NAME
import co.anbora.labs.spatiaroom.data.model.PtAmenity.Companion.TABLE_NAME_ENTITY

@Database(
    entities = [PtAmenity::class],
    version = 1,
    //exportSchema = false
)
@TypeConverters(GeometryConverters::class)
abstract class AppDatabase : RoomDatabase() {

    /**
     * @return [AmenitiesDao] Foodium Posts Data Access Object.
     */
    abstract fun getAmenity(): AmenitiesDao

    companion object {
        const val DB_NAME = "cafesSpatiaLite.sqlite";
        const val ASSET_PATH = "database/" + DB_NAME;

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val instance = SpatiaRoom.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME,
                ).createFromAsset(ASSET_PATH)
                    .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        db.beginTransaction()
                        try {
                            // Initialize metadata spatialite
                            db.query("SELECT InitSpatialMetaData();").moveToNext()

// Create a temp table with blob column geometry
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

// Add geometry column
                            db.query("""
    SELECT AddGeometryColumn(
        'amenity_internet_cafe_lisboa_2_new', 
        'GEOMETRY', 
        4326, 
        'POINT', 
        'XY'
    );
""").moveToNext()

// Create spatial index for column GEOMETRY
                            db.query("""
    SELECT CreateSpatialIndex(
        'amenity_internet_cafe_lisboa_2_new', 
        'GEOMETRY'
    );
""").moveToNext()

// Insert data from old table
                            db.execSQL("""
    INSERT INTO "amenity_internet_cafe_lisboa_2_new" 
    SELECT "ogc_fid", "full_id", "osm_id", "osm_type", "amenity", "wheelchair", "opening_hours", "name", "internet_access", "GEOMETRY" 
    FROM "amenity_internet_cafe_lisboa"
""")

                            // Delete old table with data
                            db.execSQL("DROP TABLE IF EXISTS `amenity_internet_cafe_lisboa`")
                            // Delete old table created by room entity
                            db.execSQL("DROP TABLE IF EXISTS `amenity_internet_cafe_lisboa_2`")

// Rename new table
                            db.execSQL("""
    ALTER TABLE "amenity_internet_cafe_lisboa_2_new" 
    RENAME TO "amenity_internet_cafe_lisboa_2"
""")
                        } finally {
                            db.setTransactionSuccessful()
                        }

                        db.endTransaction()
                    }
                })
                    .build()

                INSTANCE = instance
                return instance
            }
        }

    }
}
