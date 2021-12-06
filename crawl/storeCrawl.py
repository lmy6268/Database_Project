import getCU as cu, getGS as gs
from multiprocessing import Process,Manager,freeze_support as fs
import os
import pickle #에러난 리스트를 저장함.
import pymysql;from pymysql.constants import CLIENT
import category

#필요한 데이터 목록
path=os.path.dirname(os.path.realpath(__file__))
file=open(f'{path}\m.txt','r')
r=file.readlines()
ID=r[3].strip('\n');PW=r[4].strip('\n')
c=0  #api호출 시 1분당 10개의 전송밖에 허용하지 않아서

#메소드 목록
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
    with open(f'{path}error.pkl','wb') as f:
        pickle.dump(error,f)
def save_data(dic):
    reset_idp = "ALTER TABLE products AUTO_INCREMENT=1;SET @COUNT = 0;UPDATE products SET prod_id = @COUNT:=@COUNT+1;"
    reset_ids = "ALTER TABLE sales AUTO_INCREMENT=1;SET @COUNT = 0;UPDATE sales SET sal_id = @COUNT:=@COUNT+1;"
    data={"host" : "193.122.126.186",
    "port" : 3306,
    "database" : "sampleDB",
    "user" : ID,
    "password" : PW,
    "client_flag": CLIENT.MULTI_STATEMENTS}
    #sql과 통신하는 부분
    conn= pymysql.connect(**data)
    print("서버에 업로드 중")
    try:
        with conn.cursor() as curs:
                curs.execute(reset_idp)
                curs.execute(reset_ids)
        for i in range(0,len(dic)):
            for k in dic[i]:
                id_get=0
                # INSERT (물품을 넣음)
                with conn.cursor() as curs:
                    prod_sql = "INSERT INTO products(prod_name,prod_img,prod_category) values(%s,%s, %s) ON DUPLICATE KEY UPDATE prod_img = %s"
                    curs.execute(prod_sql, (k['name'],k['image'],k['category'],k['image']))
                
                conn.commit()
            
                # SELECT(물품의 아이디를 검색함)
                with conn.cursor() as curs:
                    sql = "select prod_id FROM products where prod_name = %s"
                    curs.execute(sql,k['name'])
                    rs = curs.fetchall()
                    id_get=int(rs[0][0]) #얻은 아이디 
                    
                # INSERT (물품을 sale테이블에 연관시킴)
                with conn.cursor() as curs:
                    sql = "insert into sales(store,prod_id,saletype,prod_price) values(%s,%s,%s,%s) ON DUPLICATE KEY UPDATE saletype= %s,prod_id=%s,prod_price=%s"
                    curs.execute(sql,(k['store'],id_get,k['type'],int(k['price']),k['type'],id_get,int(k['price'])))
                        
                conn.commit()
        
    finally:
            conn.close()
def get_cat(tmp):
    global c
    
    if ")" in tmp['name']:
        a=tmp['name'].split(')',1)
    else:
        a=["",tmp['name']]
    
    ty=[f"{a[0]+' '+a[1]}",a[1]]
    trash=['입','G','g','L','㎖','ML']
    size=['소','중','대','/']
    for K in ty:
        keyword=K
        if '(' in keyword:
            keyword=keyword[:keyword.index('(')]
        if keyword[-1] in trash:
            keyword=keyword[:-1]
        for i in range(len(keyword)-1,0,-1):
            if keyword[i].isdigit():
                keyword=keyword[:-1]
            else:
                break
        keyword=keyword.strip()
        if keyword[-1] in size:
            keyword=keyword[:-1]
        if keyword[-2:]=='울날':
            ty.append(keyword[:-2])
        if c>8:
            cat=category.search(keyword,c)
            c=0
        else:
            cat=category.search(keyword,c=0)    
        c+=1
        if cat!= None: #만약 해당하는 값이 있다면 값을 입력하고 종료함.
            tmp['category']=cat 
            return tmp
    return tmp
 
if __name__ == '__main__':
    fs()
    A=main()
    result=[A['GS'][0],A['CU'][0]]
    error=[A['GS'][1],A['CU'][1]]
    handleResult(result)
    handleError(error)