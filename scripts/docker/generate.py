import sys
import random
import time

# Скрипт для генерации входных данных.
# Первый параметр задаёт имя файла. Имя "1" зарезервировано для вывода в stdout (консоль)
# Остальные - макс. номер поста, пользователя (для генерации имён), типов, кол-во данных для генерации
#   и размер пакета (пакеты выдаются раз в 5 секунд)

f = None if len(sys.argv) < 2 or sys.argv[1] == "1" else open(sys.argv[1], 'w')

if len(sys.argv) < 3:
    print("Input post count: "),
    maxPost = raw_input()
else:
    maxPost = sys.argv[2]

if len(sys.argv) < 4:
    print("Input user count: "),
    maxUser = raw_input()
else:
    maxUser = sys.argv[3]

if len(sys.argv) < 5:
    print("Input type count: "),
    maxType = raw_input()
else:
    maxType = sys.argv[4]

if len(sys.argv) < 6:
    print("Input the total number of lines: "),
    cnt = raw_input()
else:
    cnt = sys.argv[5]

if len(sys.argv) < 7:
    print("Input the batch size (5s delay in between). 0 = no delays: "),
    bsize = raw_input()
else:
    bsize = sys.argv[6]

for i in range(1, int(cnt)):
    if int(bsize) > 0 and i % int(bsize) == 0:
        time.sleep(5)
    userNum = random.randrange(1, int(maxUser))
    userName = "user" + str(userNum)
    aType = random.randint(1, int(maxType))
    aTime = random.randint(1, 1000000000)
    pNum = random.randint(1, int(maxPost))
    strOut = str(pNum) + ", " + userName + ", " + str(aTime) + ", " + str(aType)
    if f is None:
        print(strOut)
    else:
        f.write(strOut + "\n")
if f is not None:
    f.close()
