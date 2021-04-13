# -*- coding: utf-8 -*-
import xlrd

field_cnt = 25

# 읽기 라이브러리를 통해 현재 같은 디렉터리상에 있는 test.xls를 불러와 workbook에 할당합니다.
workbook = xlrd.open_workbook('verb.xls')

# 워크북에 할당된 엑셀 데이터의 첫번째시트를 불러옵니다.
worksheet = workbook.sheet_by_index(0)

# nrows에 불러온 첫번째 시트의 행수를 불러옵니다.
nrows = worksheet.nrows

# 불러온 데이터를 저장할 딕셔너리를 선언합니다.
datadict = {}

# 행수만큼의 for loop 를 돌려서 행단위로 데이터를 불러와 datadict에 저장합니다.
for row_num in range(nrows):
    datadict[row_num] = {}
    # field_cnt는 열의 개수입니다. 열갯수는 여러분이 지정하시면 됩니다.
    for col in range(field_cnt):
        # datadict[row_num]에 열숫자별로 셀데이터를 저장합니다.
        datadict[row_num][col] = worksheet.cell_value(row_num, col)


num  = 0

# 컴퓨터 관련 어휘를 가져온다
for i in range(len(datadict)):
    if(str(datadict[i]).find("정보") != -1 or str(datadict[i]).find("컴퓨터") != -1  or str(datadict[i]).find("통신") != -1  or str(datadict[i]).find("전화") != -1  or str(datadict[i]).find("전자") != -1 ):
        print(str(num) + ' ', end='')
        print(datadict[i][0])
        num +=1
