import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Transaction(val id: Int, val type: String, val amount: Double, val category: String, val datetime: LocalDateTime)

data class Category(val id: Int, val name: String)

object DataManager {
    var balance: Double = 0.0
    var transactions: MutableList<Transaction> = mutableListOf()
    var categories: MutableList<Category> = mutableListOf(Category(1, "General"))
    var nextTransactionId: Int = 1
    var nextCategoryId: Int = 2

    fun addCategory(name: String) {
        categories.add(Category(nextCategoryId++, name))
    }

    fun getCategoryByName(name: String): Category? = categories.firstOrNull { it.name.equals(name, true) }
}

fun main() {
    println("Welcome to the Expense Tracker!")
    while (true) {
        println("\nAvailable commands:\n1. Show Balance\n2. Add Expense\n3. Add Income\n4. Cancel Last Transaction\n5. Show History\n6. Add Category\n7. Show Balance By Category\n0. Exit")
        println("Enter command:")
        when (readLine()) {
            "1" -> showBalance()
            "2" -> addTransaction("Expense")
            "3" -> addTransaction("Income")
            "4" -> cancelLastTransaction()
            "5" -> showHistory()
            "6" -> addCategory()
            "7" -> showBalanceByCategory()
            "0" -> {
                println("Exiting the Expense Tracker. Goodbye!")
                break
            }
            else -> println("Invalid command. Please try again.")
        }
    }
}

fun showBalance() {
    println("Current Balance: $${DataManager.balance}")
}

fun addTransaction(type: String) {
    try {
        println("Enter amount:")
        val amount = readLine()?.toDoubleOrNull() ?: throw IllegalArgumentException("Invalid amount. Please enter a positive number.")
        if (amount <= 0) throw IllegalArgumentException("Amount must be greater than 0.")

        println("Available categories: ${DataManager.categories.joinToString { it.name }}")
        println("Enter category name (or press Enter for General):")
        val categoryName = readLine()?.takeIf { it.isNotBlank() } ?: "General"
        val category = DataManager.getCategoryByName(categoryName) ?: DataManager.getCategoryByName("General")!!

        DataManager.transactions.add(Transaction(DataManager.nextTransactionId++, type, amount, category.name, LocalDateTime.now()))
        DataManager.balance += if (type == "Expense") -amount else amount

        println("$type of $$amount in '${category.name}' category added successfully.")
    } catch (e: Exception) {
        println(e.message)
    }
}

fun cancelLastTransaction() {
    if (DataManager.transactions.isEmpty()) {
        println("No transactions to cancel.")
        return
    }
    val lastTransaction = DataManager.transactions.removeAt(DataManager.transactions.size - 1)
    DataManager.balance += if (lastTransaction.type == "Expense") lastTransaction.amount else -lastTransaction.amount
    println("Last transaction canceled successfully.")
}

fun showHistory() {
    if (DataManager.transactions.isEmpty()) {
        println("Transaction history is empty.")
        return
    }
    DataManager.transactions.forEach { transaction ->
        println("${transaction.type}: $${transaction.amount} in '${transaction.category}' on ${transaction.datetime.format(DateTimeFormatter.ISO_DATE_TIME)}")
    }
}

fun addCategory() {
    println("Enter new category name:")
    val name = readLine()?.trim()
    if (name.isNullOrEmpty()) {
        println("Invalid category name.")
        return
    }
    if (DataManager.getCategoryByName(name) != null) {
        println("Category '$name' already exists.")
        return
    }
    DataManager.addCategory(name)
    println("Category '$name' added successfully.")
}
fun showBalanceByCategory() {
    DataManager.categories.forEach { category ->
        val total = DataManager.transactions.filter { it.category == category.name }
            .sumOf { if (it.type == "Expense") -it.amount else it.amount }
        println("${category.name}: $${total}")
    }
}