package co.anbora.labs.spatiaroom.data


import android.content.Context
import android.widget.Toast
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import co.anbora.labs.spatia.builder.SpatiaRoom
import co.anbora.labs.spatia.geometry.GeometryConverters
import co.anbora.labs.spatiaroom.data.dao.AddressesDao
import co.anbora.labs.spatiaroom.data.dao.AmenityDao
import co.anbora.labs.spatiaroom.data.helper.DatabaseMigrationHelper
import co.anbora.labs.spatiaroom.data.model.PtAddresses
import co.anbora.labs.spatiaroom.data.model.PtAmenity
import java.io.File


@Database(
    entities = [PtAddresses::class, PtAmenity::class],
    version = 1,
    //exportSchema = false
)
@TypeConverters(GeometryConverters::class)
abstract class AppDatabase : RoomDatabase() {

    /**
     * @return [AddressesDao]   Data Access Object.
     */
    abstract fun addresses(): AddressesDao

    /**
     * @return [AmenityDao]   Data Access Object.
     */
    abstract fun amenity(): AmenityDao

    companion object {
        const val DB_NAME = "lisbonSpatiaLite.sqlite";
        const val  DB_FOLDER_NAME = "databases"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            // Get external files directory
            val baseDir = context.getExternalFilesDir(null)

            // Create a "databases" directory within the external files directory
            val databasesDir = File(baseDir, DB_FOLDER_NAME)

            if (!databasesDir.exists()) {
                // Create the "databases" directory if it doesn't already exist
                val dirCreated = databasesDir.mkdirs()
                if (!dirCreated) {
                    throw RuntimeException("Failed to create 'databases' directory.")
                }
            }

            // Get the external file path for the database
            val dbFile = File(databasesDir, DB_NAME)

            // Check if the database file exists
            if (!dbFile.exists()) {
                // Alert the user that the file is missing and return without initializing Room
                Toast.makeText(
                    context,
                    "Database file not found. Please create the file at: " + dbFile.path,
                    Toast.LENGTH_LONG
                ).show()
                // Throw an exception to stop execution without returning an instance
                throw IllegalStateException("Database file not found.")
            }

            synchronized(this) {
                val instance = SpatiaRoom.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME,
                ).createFromFile(dbFile)
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            db.beginTransaction()
                            try {
                                // Initialize metadata spatialite
                                DatabaseMigrationHelper.initializeSpatialMetaData(db)
                                DatabaseMigrationHelper.createNewTables(db)
                                DatabaseMigrationHelper.addGeometryColumn(db)
                                DatabaseMigrationHelper.createSpatialIndex(db)
                                DatabaseMigrationHelper.migrateDataFromOriginalTable(db)
                                DatabaseMigrationHelper.dropOldTables(db)
                                DatabaseMigrationHelper.renameNewTable(db)
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
