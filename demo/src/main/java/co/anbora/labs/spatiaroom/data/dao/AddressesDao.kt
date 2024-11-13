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
import co.anbora.labs.spatiaroom.data.model.PtAddresses

/**
 * Data Access Object (DAO) for [Addresses]
 */
@Dao
interface AddressesDao {

    /**
     * Get Spatia lite version
     */
    @Query("SELECT spatialite_version()")
    @SkipQueryVerification
    fun getSpatiaVersion(): String

    /**
     * Inserts [addresses] into the [PtAddresses.TABLE_NAME] table.
     * Duplicate values are replaced in the table.
     * @param addresses Posts
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAddress(addresses: List<PtAddresses>)

    /**
     * Fetches the addresses from the [PtAddresses.TABLE_NAME] table by name
     * @return  [PtAddresses] from database table.
     */
    @Query("SELECT *, ST_AsText(geometry) AS coordinates " +
            "FROM ${PtAddresses.TABLE_NAME_ENTITY} " +
            "WHERE LOWER(city) = LOWER(:city) AND LOWER(street) = LOWER(:street) Limit 10")
    @SkipQueryVerification
    fun getAddressesByCityAndStreet(city: String, street: String): PtAddresses


    /**
     * Fetches nearby addresses within a 50 km radius from a specified location (Lisbon).
     * This query calculates the distance between each address in the `pt_addresses` table and a fixed point (Lisbon's coordinates).
     * It returns the address details (ID, country, city, street, housename, housenumber) along with the distance from the fixed point.
     *
     * The fixed location in this case is Lisbon, with coordinates at longitude -9.1395 and latitude 38.7223 (SRID 4326).
     * The distance is calculated using the `ST_Distance` function, which returns the distance in meters.
     *
     * Only addresses within a 50 km radius (50,000 meters) are returned.
     *
     * @return A list of addresses within a 50 km radius from Lisbon, including the distance to each address in meters.
     */
    @Query("""
    SELECT a.id, a.country, a.city, a.street, a.housename, a.housenumber,
           AsText(a.Geometry) AS address_coordinates,
           ST_Distance(
               a.Geometry,  -- Address coordinates from pt_addresses table
               MakePoint(-9.1395, 38.7223, 4326)  -- Lisbon location coordinates
           ) AS distance
    FROM ${PtAddresses.TABLE_NAME_ENTITY} a
    WHERE ST_Distance(
               a.Geometry,
               MakePoint(-9.1395, 38.7223, 4326)  -- Lisbon location coordinates
           ) <= 50000 Limit 20;  -- 50 km in meters and limit to 20
    """)
    @SkipQueryVerification
    fun getNearbyAddresses(): List<PtAddresses>

    /**
     * Fetches the distance between two geographical points after transforming them into a specific spatial reference system.
     * This query calculates the distance between two points, with the first point at longitude -72.1235 and latitude 42.3521,
     * and the second point at longitude -71.1235 and latitude 42.1521, both initially in the WGS 84 coordinate system (SRID 4326).
     * The points are then transformed into the spatial reference system with SRID 26986 (a UTM zone) before calculating the distance.
     *
     * @return The distance between the two points in meters, computed after the spatial transformation.
     */
    @Query("""
        SELECT ST_Distance(
            Transform(MakePoint(-72.1235, 42.3521, 4326), 26986),
            Transform(MakePoint(-71.1235, 42.1521, 4326), 26986)
        ) as distance
    """)
    @SkipQueryVerification
    fun stDistanceTest(): Double


    /**
     * Generates a polygon from a series of coordinates defining a closed line, and returns the polygon as a well-known text (WKT) string.
     * The query creates a polygon using the coordinates specified in the LINESTRING, ensuring that the first and last points are the same
     * to close the polygon. The result is returned as a WKT string representation of the polygon.
     *
     * @return The WKT string representation of the polygon, which is formed by the coordinates (0 0, 100 0, 100 100, 0 100, 0 0).
     */
    @Query("""SELECT ASText(MakePolygon(('LINESTRING(0 0, 100 0, 100 100, 0 100, 0 0)'))) as line""")
    @SkipQueryVerification
    fun makePolygonTest(): String



}