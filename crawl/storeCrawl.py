import getCU as cu, getGS as gs
from multiprocessing import Process,Manager,freeze_support as fs
import time
def main():
    procs = []
    result=Manager().list() #결과값을 저장하는 리스트
    proc1 = Process(target=cu.crawl,args=(result,))
    procs.append(proc1)
    proc2 = Process(target=gs.crawl,args=(result,))
    procs.append(proc2)
    proc1.start()
    proc2.start()
    for proc in procs:
        proc.join()
    return result
if __name__ == '__main__':
    start_t=time.time()
    fs()
    A=main()
    end_t=time.time()
    for i in A:
        print(i['store'],i['id'],i['name'],i['type'],i['price'])
    print('elapse time: ',end_t-start_t)