#-*- coding: utf-8 -*-
import wget
import pandas as pd
import os ; import re
import checkSim as cs
import time
path=os.path.dirname(os.path.realpath(__file__))
file=path+"/DB.csv"
file2=f"{path}\\수정.csv"
#필요없는 단어들을 모아두었다. => 불필요한 문자열 제거 위함
delete="[]-\",&()\'\n`**？\t·" 
delete_s=['(주)','㈜','(유)','(사)','(사복)','(영)','(재)']
delete_b=["에프엔비","에프앤지","에프앤비","에프앤씨",'시스템즈','산업','아이스크림','가루비','공업','에스지','에프앤브이','콜라','에이치티비','유한책임회사','유업','햄','제과','씨앤에프','식품','주식회사','빵굽네','음료','씨푸드','푸드','인터내셔널','라면','에스에프','제유','프리토레이','제일제당','코리아','진천BLOSSOMCAMPUS']
delete_sp=r'[가-힣]{2}공장|[가-힣]{1}[0-9]{1}공장'
delete_r=r'중기[12]\?|후기\?|완전기밥\?|[一-龥]+|\?|\"|[0-9]+g|[0-9]+개입|[0-9]+입|제[0-9]+공장|[가-힣]+[0-9]*공장|[0-9]+공장|제[0-9]+'
#필요없는 행을 제거하기 위한 리스트
drop_str=['호남','센터','베이커리','법인','협동조합','농협','배스킨라빈스','비앤비코리아','맥도날드','도미노피자','버거킹','KFC','도야지','동그린','해운대닷컴','해오름','향토농산홍삼사업부','7번가피자','깊은숲속행복한','롯데리아']

def cleanText(text):
    if text.find(" ")==-1:
        text=re.sub(delete_sp,'',text)
    else:
        text=re.sub(delete_r,'',text)
    for i in delete:
        if i in text:
            text=text.replace(i,'')
    text="".join(text.split(" "))
    return text 
def cleanText_B(text):
    for i in delete_s:
        if i in text:
            if i in text[0:3]:
                text=text.replace(i,'')
            else: #중간에 (주) 가 있는 경우엔 split
                text=text.split(i)
                text=" ".join(text)

    for i in delete_b:
        if i in text:
            text=text.replace(i,'')       
    text=cleanText(text)
    return text
def initialize(): #초기화
    
    file=path+"/DB.csv"
    if not os.path.isfile(file): #DB를 다운받는다 <- 파일이 없는 경우
        url='https://www.foodsafetykorea.go.kr/fcdb/multi/file/download.do?key=HtK2y16FQ6u7tmufhjNj/mzODIJChbAtR0F90BhWEH4AV/ufMC8q0UsclPMMhZuqLq3+fvSOmRzDh7j/xJm53A=='
        wget.download(url,out=path+'/DB.xlsx')
        xlsx = pd.read_excel(path+"/DB.xlsx")
        os.remove(path+"/DB.xlsx")
        xlsx.to_csv(file,encoding='CP949')
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
    df['식품명']=df['식품명'].apply(cleanText)
    for i in drop_str:
        df=df[~df['지역 / 제조사'].str.contains(i)]
    length=len(df)
    df=df.sort_values(by=df.columns[0],ascending=True)
    df = df.reset_index(drop=True)
    drop_list=[]
    for i in range(length):
        data=str(df.loc[i, '지역 / 제조사'])
        if data=='':
           drop_list.append(i)
        if data=='가가대소':
            break
        cur_score=cs.isKorean(data)
        if cur_score==0:
            drop_list.append(i)
    df=df.drop(drop_list)
    df['지역 / 제조사']=df['지역 / 제조사'].apply(cleanText_B)
    df['식품명']=df['지역 / 제조사']+')'+df['식품명']
    df['1회제공량']=df['1회제공량'].astype(str)+df['내용량_단위']
    df=df.drop(df.columns.difference(['식품명','1회제공량','에너지(㎉)','탄수화물(g)','총당류(g)','단백질(g)','지방(g)','총 포화 지방산(g)','콜레스테롤(g)','트랜스 지방산(g)','나트륨(㎎)']),axis=1) #필요한 부분을 제외하고 삭제하는 편이 더 빨라보임.
    #데이터 가공
    unit=[]
    column=[]
    for i in df.columns.tolist()[2:]:
        a,b=i.split('(')
        column.append(re.sub(r'[총 ]*','',a))
        unit.append(b.replace(')',''))
    df=df.replace('-',0)
    df.columns=[df.columns.tolist()[0],df.columns.tolist()[1]]+column
    for c,u in zip(column,unit):
        df[c]=df[c].astype(str)+u
    #중복데이터가 들어가 있음 과거데이터
    df=df.drop_duplicates('식품명')
    df=df.sort_values(by=df.columns[0],ascending=True)
    df = df.reset_index(drop=True)
    drop_list=[]
    for i in range(length):
        data=str(df.loc[i, '식품명']).split(')')[0]
        if data=='':
           drop_list.append(i)
        if data=='가가대소':
            break
    df=df.drop(drop_list)

    #원본파일 삭제
    os.remove(path+"/DB.csv")
    #결과파일 출력
    df.to_csv(file2,index=False, encoding="utf-8-sig")
    
def main(text):#검사할 문장을 인자로 받아온다
    if not os.path.isfile(file2) :
        initialize()
        return main()
    else:
        start_t=time.time()
        df=pd.read_csv(file2)
        #변수
        m=[]
        length=len(df)
        check=False
        count=0
        #가장 유사한 식품을 찾아 그 행의 번호를 추출한다.
        #문장 비교
        for i in range(length):
            try:
                data=df.loc[i, "식품명"]
                cur_score=cs.diff(text, data)
                if cur_score>=0.8:
                    m=[cur_score,data,i]
                    break
                if count>100:
                    break
                if cur_score>=0.7:
                    if len(m)==0:
                        m=[cur_score,data,i]
                        check=True
                    elif cur_score>m[0]:  #유사도 측정
                        m=[cur_score,data,i]
                        check=True
                else:
                    if check==True:count+=1
                    else:count=0
            except:
                continue
        if m==[]:
            return None
        else:
            return df.loc[m[-1],:].to_list() 


    
