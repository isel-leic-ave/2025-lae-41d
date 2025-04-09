class Account {
    private val created: Long
        get() = field // <=> get() { return field }

    init { // Initializer block: is called after the primary constructor
        Account.numberOfAccounts++ // <=> Account.numberOfAccounts++
        created = System.currentTimeMillis()
    }

    companion object {
        var numberOfAccounts = 0
            private set // excludes the setter method
    }
}