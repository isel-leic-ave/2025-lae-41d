// Extract the statement from the command line arguments
const statement = process.argv[2]

// Build a new function using the provided statement
const dynamicFun = Function(statement)

// Now you can call the dynamically created function
dynamicFun()