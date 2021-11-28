var router = require('express').Router();
var db = require('../models');


//사용자의 post body를 처리하기 위한 미들웨어
router.use(express.json());
router.use(express.urlencoded({
    extended: true
}))


//초기 화면 
router.get('/', function (req, res) {
    res.send('Hello World!');
});

//query param list
//store: 할인 하는 편의점 명
//cat: 상품의 카테고리
//limit: 몇개를 보고 싶은지
//offset: 몇 번째 페이지

//클라이언트에서  상품 조회
router.get('/products', (req, res) => {
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

    //쿼리문 정의 (Template Literal 사용)
    var query = `Select prod_id,sal_id,prod_img, prod_name,prod_price, saletype  
    from products as p 
    join sales as s on s.prod_id=p.prod_id 
    ${where} ${category} ${N} ${store} 
    limit ${offset},${limit}`; 
    //쿼리 실행
    db.sequelize.query(query, {
            type: db.sequelize.QueryTypes.SELECT
        })
        .then(
            data => res.json(data)).catch(err => console.log(err));
})

//영양 정보를 보여주는 루트
router.get('/nutrition', (req, res) => {
    var prodID=req.query.prodID;
    var query=`Select * from nutrition where prod_id=${prodID}`;
    db.sequelize.query(query, {
        type: db.sequelize.QueryTypes.SELECT
    })
    .then(
        data => res.json(data)).catch(err => console.log(err)); 
}); 


//개인정보를 다루는 공간(라우트)은 POST 통신을 통하여 보안성을 높임.
//클라이언트에서 회원가입 요청
router.post('/signup', (req, res) => {
    var ID = req.body.id; //아이디
    var PW = req.body.pw; //비밀번호, sha2 방식으로 저장됨.
    var EM = req.body.em; //이메일 주소
    var NN = req.body.nn; // 닉네임

    var query=`INSERT INTO USER VALUES(${ID},${PW},${NN},${EM})`; // 회원가입 쿼리
    db.sequelize.query(query, {
        type:db.sequelize.QueryTypes.INSERT
    }).then(data=>{
        res.send("회원가입이 완료되었습니다");
    });

})
//중복을 체크하는 쿼리
router.get('/duplicate', (req, res) => { 
var ID = req.query.id_check;
var Email = req.query.email_check;
var Nickname = req.query.nickname_check;
console.log(ID+Email+Nickname)
var queryT = "";
if (ID) {
    queryT = `user_id="${ID}"`
} else if (Email) {
    queryT = `user_email="${Email}"`
} else {
    queryT = `user_name="${Nickname}"`;
}
var query = `SELECT * from user WHERE ${queryT}`; // 회원정보 조회 쿼리
db.sequelize.query(query, {
        type: db.sequelize.QueryTypes.SELECT
    })
    .then(
        data => {
            if (data.length == 0) 
            {res.status(200);
            res.send("OK")
        } //데이터가 없는 경우 200 상태코드 리턴
            else
             {
                res.status(400);
                res.send("Error");}//데이터가 있는 경우,400 상태코드 리턴(접근 허가) 및 에러 값 전달
        }).catch(err => console.log(err)); //클라이언트에서 로그인 요청
});

//로그인
router.post('/login', (req, res) => {
    var ID = req.body.id; //아이디
    var PW = req.body.pass; //비밀번호, sha2 방식으로 저장됨.
    var query = `SELECT * from user WHERE user_id="${ID}" and user_pwd="${PW}"`; // 회원정보 조회 쿼리
    db.sequelize.query(query, {
            type: db.sequelize.QueryTypes.SELECT
        })
        .then(
            data => {
                if (data.length == 0) 
                {res.status(403);
                res.send("Error")
            } //데이터가 없는 경우 200 상태코드 리턴
                else
                 {
                    res.status(200);
                    res.send("OK");}//데이터가 있는 경우,400 상태코드 리턴(접근 허가) 및 에러 값 전달
            }).catch(err => console.log(err)); //클라이언트에서 로그인 요청
}) //클라이언트에서 로그인 요청



//리뷰를 보여주는 루트
router.get('/review/show',(req,res)=> {
    var salID=req.query.salID;
    var query=`select * from review where sal_id=${salID}`; //할인 상품과 관련된 리뷰를 보여줍니다.
    db.sequelize.query(query, {
        type: db.sequelize.QueryTypes.SELECT
    })
    .then(
        data => {
            res.status(200);//잘 실행된 경우
            res.json(data)})
        .catch(err =>{ // 에러난 경우
            console.log(err);
            res.status(400);
            res.send("Error");
        })
})

//리뷰를 삽입하는 루트
route.get('/review/insert',(req,res)=> {
    var salID=req.query.salID;
    var userID=req.query.userID;
    var content=req.query.content;
    var rate=req.query.rate;
    var query=`Insert INTO review(sal_id,user_id,content,rate) values(${salID},${userID},${content},${rate})`;
    db.sequelize.query(query, {
        type: db.sequelize.QueryTypes.INSERT
    })
    .then(
        data => {
            res.status(200);
            res.json("OK")})
        .catch(err =>{
            console.log(err);
            res.status(400);
            res.send("Error");
        })
})

//리뷰를 삽입하는 루트 => 한번 지우면 모든 댓글이 지워지는 문제가 생김... -> 날짜별로 아이디를 생성해야 할지 모르겠군요
route.get('/review/delete',(req,res)=> {
    var salID=req.query.salID;
    var userID=req.query.userID;
    var query=`Delete from review where sal_id="${salID}" and user_id="${userID}"`; // 회원아이디와 할인 아이디를 사용하여 리뷰를 지웁니다.
    db.sequelize.query(query, {
        type: db.sequelize.QueryTypes.DELETE
    })
    .then(
        data => {
            res.status(200);
            res.send("OK")})
        .catch(err =>{
            console.log(err);
            res.status(400);
            res.send("Error");
        })
});


//라우터 exports
module.exports=router;