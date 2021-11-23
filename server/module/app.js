const { query } = require('express');
const express = require('express'); //서버를 여는 모듈

var db = require('../models');
var app = express();
const port=3000;
app.listen(port, () => {
    console.log(`${port}포트에서 서버가 실행중입니다`);
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
    var store="";var category=""; var where="";
    var flag=false;
    var count=0; var N="";
    if (req.query.store){//상품을 판매하는 곳이 지정된 경우
        store=`%${req.query.store}%`
        store=`s.store like "${store}"`;
        flag=true;
        count+=1
    }
    if (req.query.cat){ //상품의 카테고리가 지정된 경우
        category=`p.prod_category="${req.query.cat}"`;
        flag=true;
        count+=1;
    }
    if (flag) //where절이 필요한 경우 flag On
    {
        where='where'
    }
    if (count>=2) N='and'; //where절이 길어지는 경우 and가 필요함.

    var query=`Select prod_img, prod_name,prod_price,saletype from products as p join sales as s on s.prod_id=p.prod_id ${where} ${category} ${N} ${store} limit ${Number(req.query.offset)},${Number(req.query.limit)}`;
    //쿼리 실행
    db.sequelize.query(query)
    .then(
        data => res.json(data)).catch(err => console.log(err));
}) //클라이언트에서  상품 조회


app.post('/signup', (req, res) => {
    var ID=req.body.id; //아이디
    var PW=req.body.pass;//비밀번호, sha2 방식으로 저장됨.
    var EM=req.body.email; //이메일 주소
    var NN=req.body.nn; // 닉네임
    var query=`INSERT INTO USER VALUES(${ID},${PW},${NN},${EM})`; // 회원가입 쿼리

    res.send('OK');
}) //클라이언트에서 회원가입 요청

app.post('/login', (req, res) => {
    var ID=req.body.id; //아이디
    var PW=req.body.pass;//비밀번호, sha2 방식으로 저장됨.
    var query=`SELECT * from USER WHERE user_id=${ID} and user_pwd=${PW}`; // 회원정보 조회 쿼리

}) //클라이언트에서 로그인 요청