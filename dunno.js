function fibonacci(n) {
    switch (n){
    case 0: return 0;
    case 1: return 1;
    default: return fibonacci(n - 2) + fibonacci(n - 1);
    }
}

new_var = function (integer) {
    // return Math.floor(integer); 
    
    return Math.floor.apply(integer); 
};

console.log(fibonacci(6));

console.log(new_var(3.23)); 