function fibonacci(n) {
    switch (n){
    case 0:
        return 0;

    case 1:
        return 1;

    default:
        return fibonacci(n - 2) + fibonacci(n - 1);

    }
}

console.log(fibonacci(6));