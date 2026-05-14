package com.example.pharmarestock.database

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

// --- 1. TABLES (Entities) ---

@Entity(tableName = "medicines")
data class Medicine(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
)

@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val medicineName: String,
    val quantity: Int
)

// --- 2. QUERIES (DAO) ---

@Dao
interface PharmacyDao {
    @Query("SELECT * FROM medicines WHERE name LIKE '%' || :searchQuery || '%'")
    fun searchMedicines(searchQuery: String): Flow<List<Medicine>>

    @Insert
    suspend fun insertMedicine(medicine: Medicine)

    @Insert
    suspend fun addToCart(cartItem: CartItem)

    @Query("SELECT * FROM cart_items")
    fun getCartItems(): Flow<List<CartItem>>

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()

    // --- NEW: Update an existing medicine ---
    @Update
    suspend fun updateMedicine(medicine: Medicine)

    @Delete
    suspend fun deleteMedicine(medicine: Medicine)

    @Delete
    suspend fun removeFromCart(cartItem: CartItem)

    @Query("DELETE FROM medicines")
    suspend fun clearAllMedicines()
}

// --- 3. THE DATABASE CONFIGURATION ---

@Database(entities =[Medicine::class, CartItem::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pharmacyDao(): PharmacyDao

    // This part ensures we only open ONE database connection at a time
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pharmacy_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}