const {
    query
} = require('express');
const express = require('express'); //서버를 여는 모듈

var db = require('../models');
var app = express();
const port = 3000;
const hostname = "10.0.0.226" //내부 IP 주소

//사용자의 post body를 처리하기 위한 미들웨어
app.use(express.json());
app.use(express.urlencoded({
    extended: true
}))

//서버 개방
app.listen(port, hostname, () => {
    console.log(`Server running at https://${hostname}:${port}/`);
})


app.get('/', function (req, res) {
    res.send('Hello World!');
});

//query param list
//store: 할인 하는 편의점 명
//cat: 상품의 카테고리
//limit: 몇개를 보고 싶은지
//offset: 몇 번째 페이지
app.get('/products', (req, res) => {
    //사용할 변수 초기화
    var store = "";
    var category = "";
    var where = "";
    var offset = 0; //기본값을 0으로 줌
    var limit = 20; //기본값을 20으로 줌
    var flag = false; //WHERE 표시 유무 체크용
    var count = 0; //AND 표시 유무 체크용
    var N = "";
    //분기문
    if (req.query.store) { //상품을 판매하는 곳이 지정된 경우
        store = `%${req.query.store}%`
        store = `s.store like "${store}"`;
        flag = true;
        count += 1
    }
    if (req.query.cat) { //상품의 카테고리가 지정된 경우
        category = `p.prod_category="${req.query.cat}"`;
        flag = true;
        count += 1;
    }
    if (flag) //where절이 필요한 경우 flag On
    {
        where = 'where'
    }
    if (count >= 2) N = 'and'; //where절이 길어지는 경우 and가 필요함.
    if (req.query.offset) offset = Number(req.query.offset);
    if (req.query.limit) limit = Number(req.query.limit);
    var query = `Select prod_img, prod_name,prod_price,saletype from products as p join sales as s on s.prod_id=p.prod_id ${where} ${category} ${N} ${store} limit ${offset},${limit}`;
    //쿼리 실행
    db.sequelize.query(query, {
            type: db.sequelize.QueryTypes.SELECT
        })
        .then(
            data => res.json(data)).catch(err => console.log(err));
}) //클라이언트에서  상품 조회

app.get('/nutrition', (req, res) => {
    res.send("hello WOrlkd");
}); //영양 정보를 보여주는 루트


//개인정보를 다루는 공간(라우트)은 POST 통신을 통하여 보안성을 높임.

app.post('/signup', (req, res) => {
    var ID = req.body.id; //아이디
    var PW = req.body.pw; //비밀번호, sha2 방식으로 저장됨.
    var EM = req.body.em; //이메일 주소
    var NN = req.body.nn; // 닉네임

    // var query=`INSERT INTO USER VALUES(${ID},${PW},${NN},${EM})`; // 회원가입 쿼리
    // => 아이디 중복/ 닉네임 중복 쿼리도 작성해야 함.

}) //클라이언트에서 회원가입 요청

app.get('/duplicate', (req, res) => { //중복을 체크하는 쿼리
var ID = req.query.id_check;
var Email = req.query.email_check;
var Nickname = req.query.nickname_check;
var queryT = "";
if (ID) {
    queryT = `user_id=${ID}`
} else if (Email) {
    queryT = `user_email=${Email}`
} else {
    queryT = `user_name=${Nickname}`;
}
var query = `SELECT * from user WHERE ${queryT}`; // 회원정보 조회 쿼리
db.sequelize.query(query, {
        type: db.sequelize.QueryTypes.SELECT
    })
    .then(
        data => {
            if (data.length == 0) res.status(403); //데이터가 없는 경우 403 상태코드 리턴(접근 거부)
            else res.status(200); //데이터가 있는 경우, 200 상태코드 리턴(접근 허가)
        }).catch(err => console.log(err)); //클라이언트에서 로그인 요청
});
app.post('/login', (req, res) => {
    var ID = req.body.id; //아이디
    var PW = req.body.pass; //비밀번호, sha2 방식으로 저장됨.
    var query = `SELECT * from user WHERE user_id="${ID}" and user_pwd="${PW}"`; // 회원정보 조회 쿼리
    db.sequelize.query(query, {
            type: db.sequelize.QueryTypes.SELECT
        })
        .then(
            data => {
                if (data.length == 0) res.status(403); //데이터가 없는 경우 403 상태코드 리턴(접근 거부)
                else res.status(200); //데이터가 있는 경우, 200 상태코드 리턴(접근 허가)
            }).catch(err => console.log(err));
}) //클라이언트에서 로그인 요청

//193.122.126.186:3000/duplicate?id_check==> 값을 표기 => 