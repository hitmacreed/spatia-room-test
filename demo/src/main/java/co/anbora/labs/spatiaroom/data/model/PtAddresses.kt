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

package co.anbora.labs.spatiaroom.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import co.anbora.labs.spatia.geometry.Point
import co.anbora.labs.spatiaroom.data.model.PtAddresses.Companion.TABLE_NAME_ENTITY

/**
 * Data class for Database entity and Serialization.
 */
@Entity(tableName = TABLE_NAME_ENTITY)
data class PtAddresses(

    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: Long?,

    @ColumnInfo(name = "country")
    var country: String? = null,

    @ColumnInfo(name = "city")
    var city: String?,

    @ColumnInfo(name = "postcode")
    var postcode: String?,

    var wheelchair : String?,

    @ColumnInfo(name = "street")
    var street: String?,

    @ColumnInfo(name = "housename")
    var housename: String?,

    @ColumnInfo(name = "housenumber")
    var housenumber: String?,

    @ColumnInfo(name = "Geometry")
    var geometry: Point?,
) {
    companion object {
        const val TABLE_NAME_ENTITY = "pt_addresses_cp"
    }
}
