const dotenv = require('dotenv');
const path = require('path');
dotenv.config({ path: __dirname + "/../.env" });

module.exports = { 
    development: {
      username: "skudbproject2021",  //db의 사용자명 입력
      password: process.env.PASSWORD,  //db의 비밀번호
      database: "sampleDB", 
      host: "193.122.126.186",
      dialect: "mysql",
      operatorAliases : false
    }
  }

