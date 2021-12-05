#가공된 데이터를 가지고 실제로 데이터를 데이터베이스에 저장하는 python 파일
#영양정보를 테이블에 넣는 루틴
#1. SELECT를 통해 현재 상품테이블에 있는 상품을 가져옴
#2. 각 상품의 id와 상품명을 리스트에 넣음
#3. for문을 돌라면서, 영양정보에 상품명과 일치하는 데이터를 리스트로 뽑아옴.
#4. insert를 통해 영양정보 테이블에 저장.
import pickle #에러난 리스트를 저장함.
def handleError(data):
    with open('data','wb') as f:
        pickle.dump(data,f)
#1번 부분 부터 해보자
import pymysql 
import os
from pymysql.constants import CLIENT #여러 줄의 SQL문을 돌리는 방법
import getnutrition as gn
path=os.path.dirname(os.path.realpath(__file__))
file=open(f'{path}\m.txt','r')
r=file.readlines()
ID=r[3].strip('\n');PW=r[4].strip('\n')
data={"host" : "193.122.126.186",
    "port" : 3306,
    "database" : "sampleDB",
    "user" : ID,
    "password" : PW,
    "client_flag": CLIENT.MULTI_STATEMENTS}

#sql과 통신하는 부분
conn= pymysql.connect(**data)
prod_id=[] #상품 테이블의 id
prod_name=[] #상품 테이블의 상품명
try:
    #1번 부분 
    with conn.cursor() as curs:
        sql = "select prod_id,prod_name FROM products where prod_category in('과자','식품','음료')" #데이터들중 식품들만 가져옴
        curs.execute(sql)
        rs = curs.fetchall()
        prod_id=[i[0] for i in rs]
        prod_name=[i[1] for i in rs]
    item=[]
    #2번 부분
    for i,j in zip(prod_id,prod_name):
        item.append([j,i])
    
    item=sorted(item)
    #3번 부분
    for i in item:
        result=gn.main(i[0])
        
        if result!=None:
            print(i[0],result,result[1:])
            i.extend(result[1:])
        else:
            print(i[0],result)
            i.append('None')
    handleError(item)
    #4번 부분 
    reset_idp = "ALTER TABLE nutrition AUTO_INCREMENT=1;SET @COUNT = 0;UPDATE nutrition SET id = @COUNT:=@COUNT+1;"
    with conn.cursor() as curs:
                curs.execute(reset_idp)
    with conn.cursor() as cur2:
        for i in item[0]:
            print(i[1])
            if i[-1]!="None":
                sql=f'Insert into nutrition(prod_id,por,kcal,tan,sugar,protein,fat,fofat,transfat,coles,nat) values ("{i[1]}","{i[2]}","{i[3]}","{i[4]}","{i[5]}","{i[6]}","{i[7]}","{i[8]}","{i[9]}","{i[10]}","{i[11]}")'
                cur2.execute(sql)
    conn.commit()
finally:
    conn.close()

