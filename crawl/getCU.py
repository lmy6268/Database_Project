import selenium 
from selenium import webdriver
from selenium.common.exceptions import NoSuchElementException
from selenium.common.exceptions import ElementClickInterceptedException
from selenium.common.exceptions import StaleElementReferenceException
import time
import re ; import os ;import category
def crawl(result):
    print("start CU")
    URL='http://cu.bgfretail.com/event/plus.do?category=event&depth2=1&sf=N'
    #크롬 옵션
    options=webdriver.ChromeOptions() 
    options.add_argument('window-size=1920x1080')
    options.add_argument('--disable-gpu')
    # options.add_argument('headless')
    options.add_experimental_option('excludeSwitches', ['enable-logging'])#이상한 로그 표시 지우기
    #드라이버 설정
    driver = webdriver.Chrome(f'{os.getcwd()}\crawl\chromedriver.exe',options=options)
    #URL접속
    driver.get(URL)
    driver.implicitly_wait(3)
    # count=1
    # 계속클릭
    # while 1:
    #     try:
    #         driver.find_element_by_css_selector(
    #             '#contents > div.relCon > div.prodListWrap > div > div.prodListBtn-w > a').click()
    #         print("page:",count)
    #         time.sleep(1)
    #         count+=1
    #         #클릭 가능한 객체가 없다고 나오는 경우
    #     except ElementClickInterceptedException:
    #         time.sleep(1)
    #     #DOM이 로딩이 덜 된 경우
    #     except StaleElementReferenceException:
    #         time.sleep(1)
    #     # NoSuchElementException => 더이상 불러올 정보가 없음 => 중지
    #     except NoSuchElementException:
    #         print("end")
    #         break

    goods=driver.find_elements_by_xpath('//*[@id="contents"]/div[1]/div[2]/ul/li')
    cnt=1
    res=[];error=[]
    for i in goods:
        tmp=dict()
        tmp['id']=cnt
        tmp['image']=i.find_element_by_xpath('.//a/img').get_attribute('src')
        text=i.text.split('\n')
        # A=re.sub(r'●|[N-Z]',"",text[0]).split(")")
        # if len(A)==2:
        #     tmp['brand'],tmp['name']=A[0],A[1]#브랜드 명과 상품명이 혼재된 상태, 이상한 문자들을 삭제 처리.(re.sub())
        # else:
        #     tmp['brand'],tmp['name']
        tmp['name']=text[0]
        tmp['price']=text[1][:-1].replace(",","");tmp['type']=text[2]
        tmp['store']="씨유(CU)"
        res.append(tmp)
        # cat=category.search(tmp['name'])
        # if cat!= ('', ''):
        #     tmp['category']=cat 
        #     result.append(tmp)
        # else: #현재는 이렇지만 아마 분류가 안 될 경우 관리자에게 알림을 전송하여 별도로 분류할 수 있도록 안내해야 할 것 같다.
        #     tmp['category']="결과없음"
        #     error.append(tmp)
        
        cnt+=1
    driver.close()
    result.extend(res)
    print("end CU")
    
if __name__=='__main__':
    result=[]
    crawl(result)
    print(result)