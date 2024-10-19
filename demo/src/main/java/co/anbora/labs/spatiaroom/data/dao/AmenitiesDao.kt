/*
 * MIT License
 *
 * Copyright (c) 2020 Shreyas Patil
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package co.anbora.labs.spatiaroom.data.dao

import androidx.room.*
import co.anbora.labs.spatiaroom.data.model.PtAmenity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for [dev.shreyaspatil.foodium.data.local.FoodiumPostsDatabase]
 */
@Dao
interface AmenitiesDao {

    /**
     * Inserts [amenities] into the [PtAmenity.TABLE_NAME] table.
     * Duplicate values are replaced in the table.
     * @param amenities Posts
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAmenity(amenities: List<PtAmenity>)

    /**
     * Get Spatia lite version
     */
    @Query("SELECT spatialite_version()")
    @SkipQueryVerification
    fun getSpatiaVersion(): String

    /**
     * Deletes all the amenities from the [PtAmenity.TABLE_NAME] table.
     */
    @Query("DELETE FROM ${PtAmenity.TABLE_NAME_ENTITY}")
    fun deleteAllAmenities()

    /**
     * Fetches the amenity from the [PtAmenity.TABLE_NAME] table by id
     * @param ogc_fid Unique ID of [PtAmenity]
     * @return  [PtAmenity] from database table.
     */
    @Query("SELECT * FROM ${PtAmenity.TABLE_NAME_ENTITY} WHERE ogc_fid = :id")
    @SkipQueryVerification
    fun getAmenityById(id: Int): PtAmenity

    @Query("""
        SELECT ST_Distance(
            Transform(MakePoint(-72.1235, 42.3521, 4326), 26986),
            Transform(MakePoint(-71.1235, 42.1521, 4326), 26986)
        ) as distance
    """)
    @SkipQueryVerification
    fun queryInternetCafesWithinArea(): Double


    /**
     * Fetches all the amenities from the [PtAmenity.TABLE_NAME] table.
     * @return [Flow]
     */
    @Query("SELECT * FROM ${PtAmenity.TABLE_NAME_ENTITY}")
    fun getAllAmenity(): List<PtAmenity>


}