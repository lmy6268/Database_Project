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
def handleError(error):
    with open('error.pkl','wb') as f:
        pickle.dump(error,f)

def save_data(dic):
    reset_id = "ALTER TABLE products AUTO_INCREMENT=1;SET @COUNT = 0;UPDATE products SET prod_id = @COUNT:=@COUNT+1;"
    conn={"host" : "146.56.168.221",
    "port" : 3306,
    "database" : "sampleDB",
    "user" : ID,
    "password" : PW,
    "client_flag": CLIENT.MULTI_STATEMENTS}
    table=['products','sales']
    columns=[["('prod_name','prod_img','prod_price','prod_category')"],["('store','saletype','prod_id')"]]
    # for i in dic:
        
    
    
    # insert=f"INSERT INTO {table}{columns} VALUES()"

    # with pymysql.connect(**conn) as con:
    #     for i in array:

    #     query="INSERT INTO products(prod_name,prod_);"
    #     cur = con.cursor()
    #     cur.execute()
    #     con.commit()

    #select 문
    # query = 'SELECT * from products'
    # cursor.execute(query)
    # rows=cursor.fetchall()
    # print(rows)
   
if __name__ == '__main__':
    fs()
    A=main()
    result=[A['GS'][0],A['CU'][0]]
    error=[A['GS'][1],A['CU'][1]]
    with open('result.pkl','wb') as fr:
        pickle.dump(result,fr)
    with open('error.pkl','wb') as fe:
        pickle.dump(error,fe)
    
   
    # with open('error.pkl','rb') as f:
    #     error = pickle.load(f)
    # print(len(error[1]))
   
