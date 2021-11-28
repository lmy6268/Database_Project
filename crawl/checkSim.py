#문장 간의 유사도를 측정하는 함수와 문장에 영어만 있는 지 확인하는 함수를 구현해 둠.

import difflib
from functools import reduce
import re
chut = 'ㄱㄲㄴㄷㄸㄹㅁㅂㅃㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎ#'
ga = 'ㅏㅐㅑㅒㅓㅔㅕㅖㅗㅘㅙㅚㅛㅜㅝㅞㅟㅠㅡㅢㅣ#'
ggut = ' ㄱㄲㄳㄴㄵㄶㄷㄹㄺㄻㄼㄽㄾㄿㅀㅁㅂㅄㅅㅆㅇㅈㅊㅋㅌㅍㅎ#'

BASE = 0xAC00

query = '췟'

code = ord(query) - BASE

jongsung = code % 28
jungsung = ((code-jongsung) // 28) % 21
chosung = ((code - jongsung) // 28) // 21

def segment(ch):
    '''유니코드 글자를 입력받아 초,중,종성에 대한 인덱스를 반환한다'''
    code = ord(ch) - BASE
    jongsung = code % 28
    
    code = code - jongsung
    jungsung = (code // 28) % 21
    
    code = code // 28
    chosung = code // 21
    
    if chosung < 0:
        chosung = -1
    if 19 < jongsung:
        jongsung = -1
    
    return chut[chosung], ga[jungsung], ggut[jongsung]

#유사도 측정 함수
def diff(word1, word2):
    '''두 유니코드 단어의 거리를 계산하여 차이를 반환한다'''
    L1 = ''.join(reduce(lambda x1,x2: x1+x2, map(segment, word1)))
    L2 = ''.join(reduce(lambda x1,x2: x1+x2, map(segment, word2)))
    differ = difflib.SequenceMatcher(None, L1, L2)
    return differ.ratio()

#문장에 한글이 있는지 없는 지 확인하는 함수 => 제조사 중에 영어로만 가득한 곳이 있어서.(문자열 비교에 필요없는 데이터를 줄이기 위함.)
def isKorean(text):
    hangul = re.compile('[\u3131-\u3163\uac00-\ud7a3]+')  
    result = hangul.findall(text)
    return len(result) #한글이 있다면 0이 아닌 값을 리턴함.
