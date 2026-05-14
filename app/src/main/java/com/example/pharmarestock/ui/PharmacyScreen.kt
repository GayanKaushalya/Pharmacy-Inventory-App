package com.example.pharmarestock.ui

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pharmarestock.database.CartItem
import com.example.pharmarestock.database.Medicine
import com.example.pharmarestock.database.PharmacyDao
import com.example.pharmarestock.utils.PdfGenerator
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// --- OFFICIAL WHATSAPP PALETTE ---
val WABackground = Color(0xFF121212)
val WASurface = Color(0xFF1E1E1E)
val WAGreen = Color(0xFF30883A)
val WASlate = Color(0xFF435A64)
val WAWhite = Color(0xFFFFFFFF)
val WADanger = Color(0xFFDC525A)

// --- VIEWMODEL ---
class PharmacyViewModel(private val dao: PharmacyDao) : ViewModel() {
    val allMedicines = dao.searchMedicines("").stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val cartItems = dao.getCartItems().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addToCart(name: String, quantity: Int) { viewModelScope.launch { dao.addToCart(CartItem(medicineName = name, quantity = quantity)) } }
    fun clearCart() { viewModelScope.launch { dao.clearCart() } }
    fun removeFromCart(item: CartItem) { viewModelScope.launch { dao.removeFromCart(item) } }
    fun addNewMedicine(name: String) { viewModelScope.launch { dao.insertMedicine(Medicine(name = name.trim())) } }
    fun deleteMedicine(medicine: Medicine) { viewModelScope.launch { dao.deleteMedicine(medicine) } }
    fun updateMedicine(medicine: Medicine) { viewModelScope.launch { dao.updateMedicine(medicine) } }

    fun clearEntireDatabase() {
        viewModelScope.launch {
            dao.clearCart()
            dao.clearAllMedicines()
        }
    }
}

class PharmacyViewModelFactory(private val dao: PharmacyDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T { return PharmacyViewModel(dao) as T }
}

// --- MAIN SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: PharmacyViewModel) {
    var currentTab by remember { mutableStateOf("restock") }
    val cart by viewModel.cartItems.collectAsState()

    Scaffold(
        containerColor = WABackground,
        topBar = {
            TopAppBar(
                title = {
                    val titleText = when (currentTab) {
                        "restock" -> "Daily Restock"
                        "cart" -> "Review Order"
                        "inventory" -> "Master Inventory"
                        "settings" -> "App Settings"
                        else -> ""
                    }
                    Text(titleText, fontWeight = FontWeight.Bold, fontSize = 25.sp)
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent, titleContentColor = Color.White)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.Transparent, tonalElevation = 0.dp, modifier = Modifier.padding(bottom = 8.dp)
            ) {
                val navColors = NavigationBarItemDefaults.colors(
                    selectedIconColor = WAGreen, selectedTextColor = WAGreen,
                    unselectedIconColor = Color.Gray, unselectedTextColor = Color.Gray,
                    indicatorColor = Color.Transparent
                )

                NavigationBarItem(
                    selected = currentTab == "restock", onClick = { currentTab = "restock" },
                    icon = { Icon(Icons.Default.List, contentDescription = "Restock") }, label = { Text("Restock") }, colors = navColors
                )
                NavigationBarItem(
                    selected = currentTab == "cart", onClick = { currentTab = "cart" },
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Review") }, label = { Text("Review") }, colors = navColors
                )
                NavigationBarItem(
                    selected = currentTab == "inventory", onClick = { currentTab = "inventory" },
                    icon = { Icon(Icons.Default.Build, contentDescription = "Inventory") }, label = { Text("Inventory") }, colors = navColors
                )
                NavigationBarItem(
                    selected = currentTab == "settings", onClick = { currentTab = "settings" },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") }, label = { Text("Settings") }, colors = navColors
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (currentTab) {
                "restock" -> RestockTab(viewModel, onNavigateToCart = { currentTab = "cart" })
                "cart" -> CartTab(viewModel)
                "inventory" -> InventoryTab(viewModel)
                "settings" -> SettingsTab(viewModel)
            }
        }
    }
}

// --- RESTOCK TAB ---
@Composable
fun RestockTab(viewModel: PharmacyViewModel, onNavigateToCart: () -> Unit) {
    var searchText by remember { mutableStateOf("") }
    val medicines by viewModel.allMedicines.collectAsState()
    val cart by viewModel.cartItems.collectAsState()
    var selectedMedicine by remember { mutableStateOf<Medicine?>(null) }
    var quantityText by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            TextField(
                value = searchText, onValueChange = { searchText = it },
                placeholder = { Text("Search Medicine", color = Color.White) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = WASlate) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(50),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = WASurface, unfocusedContainerColor = WASurface,
                    focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = WAGreen, focusedTextColor = WAWhite, unfocusedTextColor = WAWhite
                ),
                singleLine = true
            )

            val filtered = medicines.filter { it.name.contains(searchText, ignoreCase = true) }

            if (medicines.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No medicines yet. Go to Inventory to add some.", color = WASlate)
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(bottom = 80.dp)) {
                    items(filtered) { medicine ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp).clickable { selectedMedicine = medicine },
                            shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = WASurface), border = BorderStroke(1.dp, WASlate)
                        ) {
                            Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                                Box(modifier = Modifier.fillMaxHeight().width(6.dp).background(WAGreen))
                                Text(text = medicine.name, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp, fontWeight = FontWeight.SemiBold), color = WAWhite)
                            }
                        }
                    }
                }
            }
        }

        if (cart.isNotEmpty()) {
            Card(
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp).clickable { onNavigateToCart() },
                shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = WAGreen), elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = "Cart", tint = WAWhite)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("${cart.size} Items", fontWeight = FontWeight.Bold, color = WAWhite, fontSize = 16.sp)
                }
            }
        }
    }

    if (selectedMedicine != null) {
        AlertDialog(
            onDismissRequest = { selectedMedicine = null },
            title = { Text("Restock: ${selectedMedicine?.name}", color = WAGreen) },
            text = {
                OutlinedTextField(
                    value = quantityText, onValueChange = { quantityText = it },
                    label = { Text("Enter Quantity", color = WASlate) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = WAGreen, unfocusedBorderColor = WASlate, focusedTextColor = WAWhite, unfocusedTextColor = WAWhite)
                )
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = WAGreen, contentColor = WAWhite),
                    onClick = {
                        val q = quantityText.toIntOrNull() ?: 0
                        if (q > 0) { viewModel.addToCart(selectedMedicine!!.name, q); selectedMedicine = null; quantityText = "" }
                    }
                ) { Text("Add") }
            },
            dismissButton = { TextButton(onClick = { selectedMedicine = null }) { Text("Cancel", color = WASlate) } },
            containerColor = WASurface
        )
    }
}

// --- CART TAB ---
@Composable
fun CartTab(viewModel: PharmacyViewModel) {
    val cart by viewModel.cartItems.collectAsState()
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("GalleDrugsPrefs", Context.MODE_PRIVATE)
    val savedName = prefs.getString("userName", "") ?: ""
    val savedBranch = prefs.getString("branchName", "") ?: ""

    Column(modifier = Modifier.fillMaxSize()) {
        if (cart.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Your restock list is empty.", color = WASlate, style = MaterialTheme.typography.titleMedium)
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f), contentPadding = PaddingValues(bottom = 8.dp)) {
                items(cart) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = WASurface), border = BorderStroke(1.dp, WASlate)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                            Box(modifier = Modifier.fillMaxHeight().width(6.dp).background(WAGreen))
                            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Text(item.medicineName, style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp, fontWeight = FontWeight.SemiBold), color = WAWhite)
                                    Text("Quantity: ${item.quantity}", color = WAGreen, modifier = Modifier.padding(top = 4.dp))
                                }
                                IconButton(onClick = { viewModel.removeFromCart(item) }) { Icon(Icons.Default.Delete, contentDescription = "Remove", tint = WADanger) }
                            }
                        }
                    }
                }
            }

            val totalQuantity = cart.sumOf { it.quantity }
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = WASurface), border = BorderStroke(1.dp, WASlate)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Order Summary", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = WAWhite)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total SKUs", color = Color.Gray, fontSize = 16.sp)
                        Text("${cart.size} Items", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    HorizontalDivider(color = WASlate, thickness = 1.dp, modifier = Modifier.padding(vertical = 12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Quantity", color = Color.Gray, fontSize = 16.sp)
                        Text("$totalQuantity Units", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }

            Button(
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 8.dp).height(40.dp),
                shape = RoundedCornerShape(50), colors = ButtonDefaults.buttonColors(containerColor = WAGreen, contentColor = WAWhite),
                onClick = {
                    val pdfFile = PdfGenerator.generatePdf(context, cart, savedName, savedBranch)
                    if (pdfFile != null) {
                        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", pdfFile)
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "application/pdf"; putExtra(Intent.EXTRA_STREAM, uri); setPackage("com.whatsapp"); addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        try { context.startActivity(intent); viewModel.clearCart() }
                        catch (e: Exception) { intent.setPackage(null); context.startActivity(Intent.createChooser(intent, "Share PDF")) }
                    }
                }
            ) {
                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                Text("Send to WhatsApp", fontSize = MaterialTheme.typography.titleMedium.fontSize, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// --- INVENTORY TAB ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryTab(viewModel: PharmacyViewModel) {
    val medicines by viewModel.allMedicines.collectAsState()

    var medicineName by remember { mutableStateOf("") }
    var medicineDosage by remember { mutableStateOf("") }

    // State for the Update Popup
    var medicineToEdit by remember { mutableStateOf<Medicine?>(null) }
    var editMedicineName by remember { mutableStateOf("") }
    var editMedicineDosage by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp), shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = WASurface), border = BorderStroke(1.dp, WASlate)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Quick Entry", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = WAWhite)
                Spacer(modifier = Modifier.height(20.dp))

                Text("MEDICINE NAME", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = medicineName, onValueChange = { medicineName = it },
                    placeholder = { Text("e.g. Amoxicillin", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = WAGreen, unfocusedBorderColor = WASlate, focusedTextColor = WAWhite, unfocusedTextColor = WAWhite)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("DOSAGE (MG/ML)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = medicineDosage, onValueChange = { medicineDosage = it },
                    placeholder = { Text("e.g. 500mg", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = WAGreen, unfocusedBorderColor = WASlate, focusedTextColor = WAWhite, unfocusedTextColor = WAWhite)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = WAGreen, contentColor = WAWhite),
                    onClick = {
                        if (medicineName.isNotBlank()) {
                            val fullName = if (medicineDosage.isNotBlank()) "${medicineName.trim()} ${medicineDosage.trim()}" else medicineName.trim()
                            viewModel.addNewMedicine(fullName)
                            medicineName = ""; medicineDosage = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(40.dp), shape = RoundedCornerShape(8.dp)
                ) { Text("Register Product", fontWeight = FontWeight.Bold, fontSize = 16.sp) }
            }
        }

        Text("Current Shelf Items (${medicines.size})", modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp), color = WAGreen, fontWeight = FontWeight.Bold)

        LazyColumn(contentPadding = PaddingValues(bottom = 16.dp)) {
            items(medicines) { medicine ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = WASurface), border = BorderStroke(1.dp, WASlate)
                ) {
                    Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                        Box(modifier = Modifier.fillMaxHeight().width(6.dp).background(WAGreen))

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Weight makes sure long text doesn't push the icons out of the screen
                            Text(
                                text = medicine.name,
                                modifier = Modifier.weight(1f).padding(end = 8.dp),
                                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                                color = WAWhite
                            )

                            // Edit & Delete Icons Row
                            Row {
                                IconButton(
                                    onClick = {
                                        medicineToEdit = medicine
                                        editMedicineName = medicine.name
                                        editMedicineDosage = ""
                                    }
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = WASlate)
                                }
                                IconButton(onClick = { viewModel.deleteMedicine(medicine) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = WADanger)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- THE UPDATE POPUP ---
    if (medicineToEdit != null) {
        AlertDialog(
            onDismissRequest = { medicineToEdit = null },
            title = { Text("Edit Medicine", color = WAWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("CURRENT MEDICINE NAME", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = editMedicineName,
                        onValueChange = { editMedicineName = it },
                        modifier = Modifier.fillMaxWidth(), singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = WAGreen, unfocusedBorderColor = WASlate, focusedTextColor = WAWhite, unfocusedTextColor = WAWhite)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("ADDITIONAL DOSAGE (Optional)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = editMedicineDosage,
                        onValueChange = { editMedicineDosage = it },
                        placeholder = { Text("e.g. 250ml", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(), singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = WAGreen, unfocusedBorderColor = WASlate, focusedTextColor = WAWhite, unfocusedTextColor = WAWhite)
                    )
                }
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = WAGreen, contentColor = WAWhite),
                    onClick = {
                        if (editMedicineName.isNotBlank()) {
                            val updatedFullName = if (editMedicineDosage.isNotBlank()) "${editMedicineName.trim()} ${editMedicineDosage.trim()}" else editMedicineName.trim()
                            viewModel.updateMedicine(medicineToEdit!!.copy(name = updatedFullName))
                            medicineToEdit = null
                        }
                    }
                ) { Text("Update") }
            },
            dismissButton = { TextButton(onClick = { medicineToEdit = null }) { Text("Cancel", color = Color.White) } },
            containerColor = WASurface
        )
    }
}

// --- SETTINGS TAB ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTab(viewModel: PharmacyViewModel) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("GalleDrugsPrefs", Context.MODE_PRIVATE)

    var userName by remember { mutableStateOf(prefs.getString("userName", "") ?: "") }
    var branchName by remember { mutableStateOf(prefs.getString("branchName", "") ?: "") }

    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = WASurface), border = BorderStroke(1.dp, WASlate)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("PDF Report Details", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = WAWhite)
                Text("This information will be printed on the final PDF.", color = Color.Gray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(20.dp))

                Text("YOUR NAME", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = userName, onValueChange = { userName = it; prefs.edit().putString("userName", it).apply() },
                    placeholder = { Text("e.g. John Doe", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = WAGreen, unfocusedBorderColor = WASlate, focusedTextColor = WAWhite, unfocusedTextColor = WAWhite)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("BRANCH / STORE NAME", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = branchName, onValueChange = { branchName = it; prefs.edit().putString("branchName", it).apply() },
                    placeholder = { Text("e.g. Main Street Branch", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = WAGreen, unfocusedBorderColor = WASlate, focusedTextColor = WAWhite, unfocusedTextColor = WAWhite)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = WASurface), border = BorderStroke(1.dp, WADanger)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Danger Zone", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = WADanger)
                Text("This action is permanent and cannot be undone.", color = Color.White, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = WADanger, contentColor = WAWhite),
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.fillMaxWidth().height(40.dp), shape = RoundedCornerShape(8.dp)
                ) { Text("Delete Entire Database", fontWeight = FontWeight.Bold, fontSize = 16.sp) }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Are you absolutely sure?", color = WADanger, fontWeight = FontWeight.Bold) },
            text = { Text("This will permanently delete every single medicine from your Master Inventory and clear your current cart. You will have to register everything again.", color = WAWhite) },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = WADanger, contentColor = WAWhite),
                    onClick = { viewModel.clearEntireDatabase(); showDeleteDialog = false }
                ) { Text("Yes, Delete Everything") }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel", color = Color.White) } },
            containerColor = WASurface
        )
    }
}