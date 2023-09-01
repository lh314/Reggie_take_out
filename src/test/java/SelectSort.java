/**
@author lh
@Date 2023/8/20 11:05
@ 意图：
*/public class SelectSort {

    /**
     * 选择排序：
     * 每次遍历找到最小元素，最后将min从头开始依序赋值
     * @param a
     * @return
     */

    private int[] selectSort(int a[]) {
        for (int i = 0; i < a.length - 1; i++) {
            int minIndex = i;
            for (int j = i+1; j < a.length; j++) {
                if (a[j] < a[minIndex]) {
                    minIndex = j;
                }
            }
            if (minIndex!=i){
                int tempt = a[i];
                a[i] = a[minIndex];
                a[minIndex]= tempt;
            }
        }
        return a;
    }

    /**
     * 冒泡排序
     *1与2比交换2与3比交换3与4比交换
     * @param
     */
    private int[]  bubbleSort(int a[]){
        for(int i=0;i<a.length-1;i++){
           boolean flag = true;
           for(int j=1;j<a.length-i;j++){
               if(a[j-1]>a[j]){
                   int tempt = a[j-1];
                   a[j-1] = a[j];
                   a[j] = tempt;
                   flag = false;
               }
               if(flag){
                   break;
               }
           }
           return a;
        }
        return a;
    }

    /**
     * 自制排序
     * @param args
     */
    private int[] makeSort(int a[]){
        for (int i=0;i<a.length-1;i++){
            for(int j=i+1;j<a.length;j++){
                if(a[i]>a[j]){
                    int tempt = a [i];
                    a[i] = a [j];
                    a[j] = tempt;
                }
            }
        }
        return a;
    }


    public static void main(String[] args) {
        int a[] = {89, 22, 47, 49, 20, 48, 49};
        SelectSort selectSort = new SelectSort();
        int[] sort = selectSort.selectSort(a);
        for (int i = 0; i<sort.length;i++){
            System.out.println("选择排序："+a[i]);
        }
        int[] bubbleSort = selectSort.bubbleSort(a);
        for (int i = 0; i<bubbleSort.length;i++){
            System.out.println("冒泡排序："+a[i]);
        }
        int[] makeSort = selectSort.makeSort(a);
        for (int i = 0; i<makeSort.length;i++){
            System.out.println("自制排序："+a[i]);
        }
    }
}