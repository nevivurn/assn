import java.io.*;
import java.util.*;

public class SortingTest
{
	public static void main(String args[])
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		try
		{
			boolean isRandom = false;	// 입력받은 배열이 난수인가 아닌가?
			int[] value;	// 입력 받을 숫자들의 배열
			String nums = br.readLine();	// 첫 줄을 입력 받음
			if (nums.charAt(0) == 'r')
			{
				// 난수일 경우
				isRandom = true;	// 난수임을 표시

				String[] nums_arg = nums.split(" ");

				int numsize = Integer.parseInt(nums_arg[1]);	// 총 갯수
				int rminimum = Integer.parseInt(nums_arg[2]);	// 최소값
				int rmaximum = Integer.parseInt(nums_arg[3]);	// 최대값

				Random rand = new Random();	// 난수 인스턴스를 생성한다.

				value = new int[numsize];	// 배열을 생성한다.
				for (int i = 0; i < value.length; i++)	// 각각의 배열에 난수를 생성하여 대입
					value[i] = rand.nextInt(rmaximum - rminimum + 1) + rminimum;
			}
			else
			{
				// 난수가 아닐 경우
				int numsize = Integer.parseInt(nums);

				value = new int[numsize];	// 배열을 생성한다.
				for (int i = 0; i < value.length; i++)	// 한줄씩 입력받아 배열원소로 대입
					value[i] = Integer.parseInt(br.readLine());
			}

			// 숫자 입력을 다 받았으므로 정렬 방법을 받아 그에 맞는 정렬을 수행한다.
			while (true)
			{
				int[] newvalue = (int[])value.clone();	// 원래 값의 보호를 위해 복사본을 생성한다.

				String command = br.readLine();

				long t = System.currentTimeMillis();
				switch (command.charAt(0))
				{
					case 'B':	// Bubble Sort
						newvalue = DoBubbleSort(newvalue);
						break;
					case 'I':	// Insertion Sort
						newvalue = DoInsertionSort(newvalue);
						break;
					case 'H':	// Heap Sort
						newvalue = DoHeapSort(newvalue);
						break;
					case 'M':	// Merge Sort
						newvalue = DoMergeSort(newvalue);
						break;
					case 'Q':	// Quick Sort
						newvalue = DoQuickSort(newvalue);
						break;
					case 'R':	// Radix Sort
						newvalue = DoRadixSort(newvalue);
						break;
					case 'X':
						return;	// 프로그램을 종료한다.
					default:
						throw new IOException("잘못된 정렬 방법을 입력했습니다.");
				}
				if (isRandom)
				{
					// 난수일 경우 수행시간을 출력한다.
					System.out.println((System.currentTimeMillis() - t) + " ms");
				}
				else
				{
					// 난수가 아닐 경우 정렬된 결과값을 출력한다.
					for (int i = 0; i < newvalue.length; i++)
					{
						System.out.println(newvalue[i]);
					}
				}

			}
		}
		catch (IOException e)
		{
			System.out.println("입력이 잘못되었습니다. 오류 : " + e.toString());
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	private static int[] DoBubbleSort(int[] value)
	{
		for (int i = 0; i < value.length-1; i++) {
			for (int j = 0; j < value.length-i-1; j++) {
				if (value[j] > value[j+1]) {
					int tmp = value[j];
					value[j] = value[j+1];
					value[j+1] = tmp;
				}
			}
		}
		return (value);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	private static int[] DoInsertionSort(int[] value)
	{
		for (int i = 1; i < value.length; i++) {
			for (int j = 0; j < i; j++) {
				if (value[i] < value[j]) {
					int tmp = value[i];
					value[i] = value[j];
					value[j] = tmp;
				}
			}
		}
		return (value);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	private static int[] DoHeapSort(int[] value)
	{
		// Heapify
		for (int i = (value.length-1)/2; i >= 0; i--) {
			heapDown(value, i, value.length);
		}

		for (int i = 0; i < value.length-1; i++) {
			// Swap
			int tmp = value[0];
			value[0] = value[value.length-i-1];
			value[value.length-i-1] = tmp;
			// And heapdown
			heapDown(value, 0, value.length-i-1);
		}
		return (value);
	}

	private static void heapDown(int[] arr, int i, int n) {
		while (i*2 + 1 < n) {
			int child = i*2 + 1;
			// Select larger child
			if (child+1 < n && arr[child] < arr[child+1]) child++;
			// Determine whether to swap
			if (arr[i] >= arr[child]) return;
			// Swap
			int tmp = arr[i];
			arr[i] = arr[child];
			arr[child] = tmp;
			i = child;
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	private static int[] DoMergeSort(int[] value)
	{
		int[] aux = new int[value.length]; // Declare here to avoid allocs
		// hi is inclusive
		mergeSort(value, aux, 0, value.length-1);
		return (value);
	}

	private static void mergeSort(int[] arr, int[] aux, int lo, int hi) {
		if (lo >= hi) return;
		int mid = (lo + hi) / 2;
		mergeSort(arr, aux, lo, mid);
		mergeSort(arr, aux, mid+1, hi);
		mergeSortMerge(arr, aux, lo, mid+1, hi);
	}
	
	private static void mergeSortMerge(int[] arr, int[] aux, int a, int b, int hi) {
		int i = 0, j = 0;
		while (a+i < b && b+j <= hi) {
			if (arr[a+i] < arr[b+j]) {
				aux[i+j] = arr[a+i];
				i++;
			} else {
				aux[i+j] = arr[b+j];
				j++;
			}
		}
		while (a+i < b) {
			aux[i+j] = arr[a+i];
			i++;
		}
		while (b+j <= hi) {
			aux[i+j] = arr[b+j];
			j++;
		}
		
		for (int k = 0; k < i+j; k++) {
			arr[a+k] = aux[k];
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	private static int[] DoQuickSort(int[] value)
	{
		// hi is inclusive
		quickSort(value, 0, value.length-1);
		return (value);
	}

	private static void quickSort(int[] arr, int lo, int hi) {
		if (lo >= hi) return;

		int plo = lo-1, phi = hi+1;
		int pivot = arr[(lo+hi)/2];
		while (plo < phi) { // Exit condition never run, only for clarity
			do plo++; while (arr[plo] < pivot);
			do phi--; while (arr[phi] > pivot);
			if (plo >= phi) break;

			int tmp = arr[plo];
			arr[plo] = arr[phi];
			arr[phi] = tmp;
		}

		quickSort(arr, lo, phi);
		quickSort(arr, phi+1, hi);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	private static int[] DoRadixSort(int[] value)
	{
		int[] aux = new int[value.length];
		int[][] count = new int[4][256];

		// Counting
		for (int i = 0; i < value.length; i++) {
			int v = value[i];
			count[0][v & 0xff]++;
			count[1][(v >>> 8) & 0xff]++;
			count[2][(v >>> 16) & 0xff]++;
			count[3][(v >>> 24) & 0xff]++;
		}
		// Prefix sum
		for (int i = 1; i < 256; i++) {
			count[0][i] += count[0][i-1];
			count[1][i] += count[1][i-1];
			count[2][i] += count[2][i-1];
			count[3][i] += count[3][i-1];
		}

		// Output
		for (int i = 0; i < 4; i++) { // Exit condition never run, only for clarity
			// Populate aux
			for (int j = value.length-1; j >= 0; j--) {
				aux[--count[i][(value[j] >>> (i*8)) & 0xff]] = value[j];
			}

			// Copy back
			if (i == 3) break; // Avoid unnecessary copy
			for (int j = 0; j < value.length; j++) {
				value[j] = aux[j];
			}
		}

		// Re-order negative values
		int start, end;
		for (start = 0; start < value.length && aux[start] >= 0; start++);
		for (end = 0; start+end < value.length; end++) {
			value[end] = aux[start+end];
		}
		for (int i = 0; i < start; i++) {
			value[i+end] = aux[i];
		}

		return (value);
	}
}
