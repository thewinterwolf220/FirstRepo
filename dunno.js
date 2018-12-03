function fibonacci(n) {
    switch (n){
    case 0: return 0;
    case 1: return 1;
    default: return fibonacci(n - 2) + fibonacci(n - 1);
    }
}
function two_to_the(n){
    if (n === 0)
        return 1;
    else 
        return 2 * two_to_the(n-1);
}

console.log(two_to_the(5)) // Stands for 2 ^ 4 