package co.anbora.labs.spatiaroom.data


import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import co.anbora.labs.spatia.builder.SpatiaRoom
import co.anbora.labs.spatia.geometry.GeometryConverters
import co.anbora.labs.spatiaroom.data.dao.AmenitiesDao
import co.anbora.labs.spatiaroom.data.model.PtAmenity
import co.anbora.labs.spatiaroom.data.model.PtAmenity.Companion.TABLE_NAME

@Database(
    entities = [PtAmenity::class],
    version = 1,
    exportSchema = false
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
                        // Initialize Spatialite
                        db.query("SELECT InitSpatialMetaData(1);").moveToNext()
                        // RecoverGeometryColumn to correctly initialize Spatialite's metadata
                        db.query("SELECT RecoverGeometryColumn($TABLE_NAME, 'GEOMETRY', 4326, 'POINT', 'XY');")
                            .moveToNext()

                        db.query("SELECT CreateSpatialIndex($TABLE_NAME, 'GEOMETRY');")
                            .moveToNext()
                    }
                }).build()

                INSTANCE = instance
                return instance
            }
        }

    }
}
