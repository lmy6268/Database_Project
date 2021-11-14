import getCU as cu, getGS as gs
from multiprocessing import Process,Manager,freeze_support as fs
import os
import pickle #에러난 리스트를 저장함.
import pymysql;from pymysql.constants import CLIENT
file=open(f'{os.getcwd()}\m.txt','r')
r=file.readlines()
ID=r[3].strip('\n');PW=r[4].strip('\n')
def main():
    procs = []
    result=Manager().dict() #결과값을 저장하는 리스트
    proc1 = Process(target=cu.crawl,args=(result,))
    procs.append(proc1)
    proc2 = Process(target=gs.crawl,args=(result,))
    procs.append(proc2)
    proc1.start()
    proc2.start()
    for proc in procs:
        proc.join()
    return result
def handleResult(result):
    save_data(result)
def handleError(error):
    with open('error.pkl','wb') as f:
        pickle.dump(error,f)
def save_data(dic):
    reset_idp = "ALTER TABLE products AUTO_INCREMENT=1;SET @COUNT = 0;UPDATE products SET prod_id = @COUNT:=@COUNT+1;"
    reset_ids = "ALTER TABLE sales AUTO_INCREMENT=1;SET @COUNT = 0;UPDATE sales SET sal_id = @COUNT:=@COUNT+1;"
    data={"host" : "146.56.168.221",
    "port" : 3306,
    "database" : "sampleDB",
    "user" : ID,
    "password" : PW,
    "client_flag": CLIENT.MULTI_STATEMENTS}
    #sql과 통신하는 부분
    conn= pymysql.connect(**data)
    try:
        with conn.cursor() as curs:
                curs.execute(reset_idp)
                curs.execute(reset_ids)
        for i in range(0,len(dic)):
            for k in dic[i]:
                id_get=0
                # INSERT (물품을 넣음)
                with conn.cursor() as curs:
                    prod_sql = "INSERT INTO products(prod_name,prod_img,prod_price,prod_category) values(%s,%s, %s, %s) ON DUPLICATE KEY UPDATE prod_img = %s,prod_price= %s"
                    curs.execute(prod_sql, (k['name'],k['image'],int(k['price']),k['category'],k['image'],int(k['price'])))
                
                conn.commit()
            
                # SELECT(물품의 아이디를 검색함)
                with conn.cursor() as curs:
                    sql = "select prod_id FROM products where prod_name = %s"
                    curs.execute(sql,k['name'])
                    rs = curs.fetchall()
                    id_get=int(rs[0][0]) #얻은 아이디 
                    
                # INSERT (물품을 sale테이블에 연관시킴)
                with conn.cursor() as curs:
                    sql = "insert into sales(store,prod_id,saletype) values(%s,%s,%s) ON DUPLICATE KEY UPDATE saletype= %s,prod_id=%s"
                    curs.execute(sql,(k['store'],id_get,k['type'],k['type'],id_get))
                        
                conn.commit()
        
    finally:
            conn.close()

 
if __name__ == '__main__':
    fs()
    A=main()
    result=[A['GS'][0],A['CU'][0]]
    error=[A['GS'][1],A['CU'][1]]
    handleResult(result)
    handleError(error)


   
