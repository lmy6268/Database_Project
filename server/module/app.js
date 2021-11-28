
const express = require('express'); //서버를 여는 모듈
const router=require('../routes/routes'); //route 모듈을 가져옴 => 라우터를 위함.
var app = express();
const port = 3000;
const hostname = "10.0.0.226" //내부 IP 주소

app.use(express.json());
app.use(express.urlencoded({
    extended: true
}));
//서버 개방
app.listen(port, hostname, () => {
    console.log(`Server running at https://${hostname}:${port}/`);
})

app.use('/',router); //라우터 사용

