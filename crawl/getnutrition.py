import wget
import pandas as pd
import os ; import re
path=os.path.dirname(os.path.realpath(__file__))

def initialize(): #초기화
    # #DB를 다운받는다
    file=path+"/DB.csv"
    if not os.path.isfile(file):
        url='https://www.foodsafetykorea.go.kr/fcdb/multi/file/download.do?key=HtK2y16FQ6u7tmufhjNj/mzODIJChbAtR0F90BhWEH4AV/ufMC8q0UsclPMMhZuqLq3+fvSOmRzDh7j/xJm53A=='
        wget.download(url,out=path+'/DB.xlsx')
        xlsx = pd.read_excel(path+"/DB.xlsx")
        os.remove(path+"/DB.xlsx")
        xlsx.to_csv(path+"/DB.csv",encoding='CP949')
    #csv를 읽어옴
    df=pd.read_csv(f"{path}/DB.csv",  encoding='CP949')
    # NULL 값으로 채워진 행 제거
    df=df.dropna()
    #컬럼명 변경
    df=df.rename(columns=df.iloc[0])
    df=df.drop(df.index[0])
    df=df.set_index('NO') 
    #사용할 데이터
    df=df[df['DB군']=='가공식품']
    df=df.drop(df.columns.difference(['식품명','에너지(㎉)','탄수화물(g)','총당류(g)','단백질(g)','지방(g)','총 포화 지방산(g)','콜레스테롤(g)','트랜스 지방산(g)','나트륨(㎎)']),axis=1) #필요한 부분을 제외하고 삭제하는 편이 더 빨라보임.
    #데이터 가공
    unit=[]
    column=[]
    for i in df.columns.tolist()[1:]:
        a,b=i.split('(')
        column.append(re.sub(r'[총 ]*','',a))
        unit.append(b.replace(')',''))
    df=df.replace('-',0)
    df.columns=[df.columns.tolist()[0]]+column
   
    for c,u in zip(column,unit):
        df[c]=df[c].astype(str)+u
   
    df['식품명'] = df['식품명'].replace(r'[중기1\?]|[후기\?]|[완전기밥\?][&]*|[ (一-龥)]+', '',regex=True)
    df['식품명'] = df['식품명'].replace(r'\"', '',regex=True)
   
    #원본파일 삭제
    os.remove(path+"/DB.csv")
    #결과파일 출력
    df.to_csv(f"{path}/수정.csv",index=False, encoding="utf-8-sig")
    #1118완료 내용 >> 데이터를 온전히 불러와서 처리까지 함. 다만 각 값의 단위가 제대로 들어가지 않아서 그부분을 수정하고, pymysql을 이용하여 데이터를 삽입해야할 것으로 보임.

file2=path+'/수정.csv'
if not os.path.isfile(file2) :
    initialize()
