from selenium import webdriver
from selenium.common.exceptions import NoSuchElementException,ElementClickInterceptedException,StaleElementReferenceException
from lxml.html import fromstring,tostring
import time
import pickle
from tqdm import tqdm
import re ; import os ;import category;
import storeCrawl as sc
path=os.path.dirname(os.path.realpath(__file__))
        
def crawl(items):
    result=[]
    print("start CU")
    URL='http://cu.bgfretail.com/event/plus.do?category=event&depth2=1&sf=N'
    #크롬 옵션
    options=webdriver.ChromeOptions() 
    options.add_argument('window-size=1920x1080')
    options.add_argument('--disable-gpu')
    # options.add_argument('headless')
    options.add_experimental_option('excludeSwitches', ['enable-logging'])#이상한 로그 표시 지우기
    #드라이버 설정
    driver = webdriver.Chrome(f'{path}\chromedriver.exe',options=options)
    #URL접속
    driver.get(URL)
    driver.implicitly_wait(3)
    count=1
    # 계속클릭
    
    while 1:
        try:
            driver.find_element_by_css_selector(
                '#contents > div.relCon > div.prodListWrap > div > div.prodListBtn-w > a').click()
            print("CU page: ",count)
            time.sleep(0.3)
            count+=1
            #클릭 가능한 객체가 없다고 나오는 경우
        except ElementClickInterceptedException:
            time.sleep(0.5)
        #DOM이 로딩이 덜 된 경우
        except StaleElementReferenceException:
            time.sleep(0.5)
        # NoSuchElementException => 더이상 불러올 정보가 없음 => 중지
        except NoSuchElementException:
            print("end")
            break

    parser=fromstring(driver.page_source)
    goods=parser.xpath('//*[@id="contents"]/div[1]/div[2]/ul/li')
    res=[];error=[]
    for i in goods:
        tmp=dict()
        text=list(i for i in re.sub(r'[\t]',"",i.text_content()).split('\n') if i!="")
        tmp['name']=re.sub(r' |●|[N-Z]',"",text[0])#브랜드 명과 상품명이 혼재된 상태, 이상한 문자들을 삭제 처리.(re.sub())
        tmp['price']=text[1][:-1].replace(",","");tmp['image']=i.xpath('.//a/img')[0].get('src')
        tmp['type']=text[2]
        tmp['store']="씨유(CU)"
        tmp=sc.get_cat(tmp)
        if tmp.get('category') !=None:
            res.append(tmp)
        else:
            error.append(tmp)
    driver.close()
    result.append(res)
    result.append(error)
    items['CU']=result
    print("end CU")
    return items
    
if __name__=='__main__':
    # # print(result['CU'][1])
    k={}
    k=crawl(k)
    print(k)
    # with open('result.pkl','rb') as f:
    #     result = pickle.load(f)
    # print(result[0])
   