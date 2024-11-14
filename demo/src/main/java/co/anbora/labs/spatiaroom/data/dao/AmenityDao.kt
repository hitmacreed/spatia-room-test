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

/**
 * Data Access Object (DAO) for [Amenity]
 */
@Dao
interface AmenityDao {


    /**
     * Fetches nearby hospitals within a 50 km radius from a specified location (Lisbon).
     * This query calculates the distance between each hospital in the `pt_amenity` table and a fixed point (Lisbon's coordinates).
     * It returns the hospital details (name) along with the distance from the fixed point.
     *
     * The fixed location in this case is Lisbon, with coordinates at longitude -9.1395 and latitude 38.7223 (SRID 4326).
     * The distance is calculated using the `ST_Distance` function, which returns the distance in meters.
     *
     * Only hospitals within a 50 km radius (50,000 meters) are returned.
     *
     * @return A list of hospitals within a 50 km radius from Lisbon, including the distance to each hospital in meters.
     */
    @Query("""
    SELECT id, name, geometry,sub_type,
           ST_Distance(amenity.Geometry, MakePoint(-9.1395, 38.7223, 4326)) AS distance
    FROM ${PtAmenity.TABLE_AMENITY_ENTITY} amenity
    WHERE sub_type = 'hospital'
      AND ST_Distance(amenity.Geometry, MakePoint(-9.1395, 38.7223, 4326)) <= 50000  -- Radius of 50 km
    ORDER BY distance
    LIMIT 20
    """)
    @SkipQueryVerification
    fun getNearbyHospitals(): List<PtAmenity>


}