#INITIALIZE
import selenium
from selenium import webdriver
from selenium.common.exceptions import StaleElementReferenceException,NoSuchElementException,ElementClickInterceptedException
import re; import os
#대기를 위한 패키지
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
import time
from lxml.html import fromstring,tostring
from tqdm import tqdm
#외부 파이썬 파일 import  
import storeCrawl as sc

# 함수 정의
def checkLocated(driver,time,type,text):
    if type=='XPATH':
        return WebDriverWait(driver, time).until(EC.presence_of_element_located((By.XPATH,text)))
    if type=='CSS':
        return WebDriverWait(driver, time).until(EC.presence_of_element_located((By.CSS_SELECTOR,text)))
    if type=='ID':
        return WebDriverWait(driver, time).until(EC.presence_of_element_located((By.ID,text)))

def checkClickable(driver,type,time,text):
    if type=='XPATH':
        return WebDriverWait(driver, time).until(EC.element_to_be_clickable((By.XPATH,text)))
    if type=='CSS':
        return WebDriverWait(driver, time).until(EC.element_to_be_clickable((By.CSS_SELECTOR,text)))
    if type=='ID':
        return WebDriverWait(driver, time).until(EC.element_to_be_clickable((By.ID,text)))

#시작점과 끝점을 찾는 메소드
def findNum(driver,index,btn_on): 
    checkClickable(driver,'XPATH',3, index).click()
    return int(checkLocated(driver,3,'CSS',btn_on).text)
    
#페이지를 넘기는 메소드
def pageMove(MOD,driver,i,next): 
    try:
        if i%10==1:
            driver.find_element_by_xpath(next).click()
        elif i%10==0:
            driver.find_element_by_xpath(f'//*[@id="contents"]/div[2]/div[3]/div/div/div[{MOD+1}]/div/span/a[10]').click()
        else:
            driver.find_element_by_xpath(f'//*[@id="contents"]/div[2]/div[3]/div/div/div[{MOD+1}]/div/span/a[{i%10}]').click()
    except ElementClickInterceptedException: #만약 div가 클릭할 요소를 감싸서 클릭을 못하는 경우
        time.sleep(1)
        print("error")
        pageMove(MOD,driver, i)
#각 페이지별 데이터를 파싱하는 메소드
def parseData(MOD,driver,error): 
    parser=fromstring(driver.page_source)
    res=[]
    time.sleep(1)
    for i in range(1,9):
        try:
            contentPath=f'//*[@id="contents"]/div[2]/div[3]/div/div/div[{MOD+1}]/ul/li[{i}]'
            tmp={}
            tmp['name']=parser.xpath(f"{contentPath}/div/p[2]")[0].text
            tmp['price']=int(parser.xpath(f"{contentPath}/div/p[3]/span")[0].text.replace(',',''))
            tmp['image']=parser.xpath(f'{contentPath}/div/p[1]/img')[0].get('src')
            tmp['type']=parser.xpath(f"{contentPath}/div/div/p/span")[0].text
            tmp['store']="지에스25(GS25)"
            #카테고리
            tmp=sc.get_cat(tmp)
            if tmp.get('category')!=None:
                res.append(tmp)
            else:
                error.append(tmp)
        except NoSuchElementException:
            break
    return res

#크롤링을 진행하는 메소드 
def crawl(items):
    print("start GS")
    result=[]
    # URL 지정
    URL = 'http://gs25.gsretail.com/gscvs/ko/products/event-goods#;'

    # 크롬 옵션
    options = webdriver.ChromeOptions()
    options.add_argument('window-size=1920x1080')
    options.add_argument('--disable-gpu')
    options.add_argument('headless')
    options.add_experimental_option(
        'excludeSwitches', ['enable-logging'])  # 이상한 로그 표시 지우기

    # 드라이버 설정
    driver = webdriver.Chrome(f'{os.getcwd()}\chromedriver.exe', options=options)
    start_t=time.time()

    # 웹사이트 연결
    driver.get(URL)

    # 페이지 목록 확인
    #결과값 저장할 리스트(res) 설정 및 id 설정 변수 지정 / MOD(덤 타입): 1+1 - MOD 0, 2+1 - MOD 1
    res=[]
    error=[]
    for MOD in range(0,1):
        if MOD==0:
            checkClickable(driver,'ID', 3, 'ONE_TO_ONE').click()
        else:
            checkClickable(driver,'ID', 3, 'TWO_TO_ONE').click()
        #1. 각 버튼별 변수 지정
        #start: 맨 처음으로 돌아가는 버튼 / prev: 한 단계 이전으로 /next: 한 단계 다음으로 / end: 맨 끝으로 가는 버튼 / btn_on: 현재 활성화되어있는 페이지 번호
        start=f'//*[@id="contents"]/div[2]/div[3]/div/div/div[{MOD+1}]/div/a[1]';prev=f'//*[@id="contents"]/div[2]/div[{MOD+1}]/div/div/div[4]/div/a[2]'
        next=f'//*[@id="contents"]/div[2]/div[3]/div/div/div[{MOD+1}]/div/a[3]';end=f'//*[@id="contents"]/div[2]/div[3]/div/div/div[{MOD+1}]/div/a[4]'
        btn_on=f'#contents > div.cnt > div.cnt_section.mt50 > div > div > div:nth-child({2*MOD+3}) > div > span > a.on'
        #3. 시작과 끝점 확인 
        end_num,start_num=0,0
        while True:
            try :
                end_num=findNum(driver,end, btn_on)
                start_num=findNum(driver, start, btn_on)
                if end_num !=0 and start_num !=0:
                    break
            except StaleElementReferenceException:
                time.sleep(1)
        #4. 데이터 수집 시작 
        for i in range(start_num,end_num+1):
            print("GS page: ",i)
            time.sleep(0.3)
            try:
                pageMove(MOD,driver, i,next)
                data=parseData(MOD,driver,error)  
                res.extend(data)
            except StaleElementReferenceException:
                time.sleep(1)
                pageMove(MOD,driver, i,next)
                data=parseData(MOD,driver,error)  
                res.extend(data)
    # print(error)
    driver.quit()
    print("end GS")
    result.append(res)
    result.append(error)
    items['GS']=result
    return items

if __name__=='__main__':
    result={}
    result=crawl(result)
    print(result['GS'][0])