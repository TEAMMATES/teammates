function generateRandomString(len){
    var chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz";
    var data = '';

    for (var i=0; i<len; i++) {
        var rnum = Math.floor(Math.random() * chars.length);
        data += chars.substring(rnum,rnum+1);
    }

    return data;
}