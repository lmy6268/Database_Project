import requests
import xmltodict
import json
import os 
#정해진 카테고리 분류: 음료 / 아이스크림 / 즉석식품/ 과자 / 생활용품 
#생활용품, 음료인 경우에는 중분류에서 해결됨 /
#가공식품에서 갈림. => 소분류 확인
snack=("과자")
file=open(f'{os.getcwd()}\crawl\m.txt','r')
r=file.readlines()
ID=r[0].strip('\n');PW=r[1].strip('\n')
def search(input):
    #검색 키워드 입력
    keyword = input
    #조회
    url = f'https://openapi.naver.com/v1/search/shop.json?query={keyword}&display=1'
    header = {'HOST': 'openapi.naver.com',
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.81 Safari/537.36', 'Accept': '* /',
            'X-Naver-Client-Id': f'{ID}',
            'X-Naver-Client-Secret': f'{PW}'}
    #요청 및 응답
    res = requests.get(url, headers=header)
    result=res.text
    #전처리 과정
    find1=result.find("category2");end1=result[find1:].find('\",')
    find2=result.find("category3");end2=result[find2:].find('\",')
    #아마 아래 중분류 소분류에서 이중 체크를 거쳐서 최종 카테고리를 분류하는 방식으로 해야 할 것 같다. 
    cat1=result[find1:find1+end1+1].replace("\"", "").replace("category2: ", "")#중분류
    cat2=result[find2:find2+end2+1].replace("\"", "").replace("category3: ", "")#소분류 
    #결과 출력
    # if cat1=="생활용품":
    #     print(cat1)
    # elif cat1=="음료":
    #     print(cat1)
    # else:
    #     if cat2 in 
    return (cat1,cat2)