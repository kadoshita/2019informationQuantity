package s4.B193319;

import java.lang.*;
import java.util.*;

import s4.specification.*;

/*package s4.specification;
  ここは、１回、２回と変更のない外部仕様である。
  public interface FrequencerInterface {     // This interface provides the design for frequency counter.
  void setTarget(byte  target[]); // set the data to search.
  void setSpace(byte  space[]);  // set the data to be searched target from.
  int frequency(); //It return -1, when TARGET is not set or TARGET's length is zero
  //Otherwise, it return 0, when SPACE is not set or SPACE's length is zero
  //Otherwise, get the frequency of TAGET in SPACE
  int subByteFrequency(int start, int end);
  // get the frequency of subByte of taget, i.e target[start], taget[start+1], ... , target[end-1].
  // For the incorrect value of START or END, the behavior is undefined.
  }
*/

public class Frequencer implements FrequencerInterface {
	// Code to start with: This code is not working, but good start point to work.
	byte[] myTarget;
	byte[] mySpace;
	boolean targetReady = false;
	boolean spaceReady = false;

	int[] suffixArray; // Suffix Arrayの実装に使うデータの型をint []とせよ。

	// The variable, "suffixArray" is the sorted array of all suffixes of mySpace.
	// Each suffix is expressed by a integer, which is the starting position in
	// mySpace.

	// The following is the code to print the contents of suffixArray.
	// This code could be used on debugging.

	private void printSuffixArray() {
		if (spaceReady) {
			for (int i = 0; i < mySpace.length; i++) {
				int s = suffixArray[i];
				for (int j = s; j < mySpace.length; j++) {
					System.out.write(mySpace[j]);
				}
				System.out.write('\n');
			}
		}
	}

	private int suffixCompare(int i, int j) {
		// suffixCompareはソートのための比較メソッドである。
		// 次のように定義せよ。
		// comparing two suffixes by dictionary order.
		// suffix_i is a string starting with the position i in "byte [] mySpace".
		// Each i and j denote suffix_i, and suffix_j.
		// Example of dictionary order
		// "i" < "o" : compare by code
		// "Hi" < "Ho" ; if head is same, compare the next element
		// "Ho" < "Ho " ; if the prefix is identical, longer string is big
		//
		// The return value of "int suffixCompare" is as follows.
		// if suffix_i > suffix_j, it returns 1
		// if suffix_i < suffix_j, it returns -1
		// if suffix_i = suffix_j, it returns 0;

		// ここにコードを記述せよ
		//

		int k = 0;
		for (; i + k < mySpace.length && j + k < mySpace.length;) {
			int i_k = i + k;
			int j_k = j + k;
			int mySpace_i_k = (int) mySpace[i_k];
			int mySpace_j_k = (int) mySpace[j_k];
			if (mySpace_i_k < mySpace_j_k) {
				return -1;
			} else if (mySpace_i_k > mySpace_j_k) {
				return 1;
			}
			if ((i_k + 1) == mySpace.length && (j_k + 1) == mySpace.length) {
				return 0;
			} else if ((i_k + 1) == mySpace.length) {
				return -1;
			} else if ((j_k + 1) == mySpace.length) {
				return 1;
			}
			k++;
		}
		return 0;
	}

	public void setSpace(byte[] space) {
		// suffixArrayの前処理は、setSpaceで定義せよ。
		mySpace = space;
		if (mySpace.length > 0)
			spaceReady = true;
		// First, create unsorted suffix array.
		suffixArray = new int[space.length];
		// put all suffixes in suffixArray.
		for (int i = 0; i < space.length; i++) {
			suffixArray[i] = i; // Please note that each suffix is expressed by one integer.
		}
		//
		// ここに、int suffixArrayをソートするコードを書け。
		// 順番はsuffixCompareで定義されるものとする。

		List<Integer> suffixArrayList = new ArrayList<>();
		for (int i : suffixArray) {
			suffixArrayList.add(i);
		}
		Collections.sort(suffixArrayList, (i, j) -> suffixCompare(suffixArray[i], suffixArray[j]));
		for (int i = 0; i < suffixArrayList.size(); i++) {
			suffixArray[i] = suffixArrayList.get(i);
		}
	}

	// Suffix Arrayを用いて、文字列の頻度を求めるコード
	// ここから、指定する範囲のコードは変更してはならない。

	public void setTarget(byte[] target) {
		myTarget = target;
		if (myTarget.length > 0)
			targetReady = true;
	}

	public int frequency() {
		if (targetReady == false)
			return -1;
		if (spaceReady == false)
			return 0;
		return subByteFrequency(0, myTarget.length);
	}

	public int subByteFrequency(int start, int end) {
		/*
		 * This method be work as follows, but much more efficient int spaceLength =
		 * mySpace.length; int count = 0; for(int offset = 0; offset< spaceLength - (end
		 * - start); offset++) { boolean abort = false; for(int i = 0; i< (end - start);
		 * i++) { if(myTarget[start+i] != mySpace[offset+i]) { abort = true; break; } }
		 * if(abort == false) { count++; } }
		 */
		int first = subByteStartIndex(start, end);
		int last1 = subByteEndIndex(start, end);
		return last1 - first;
	}
	// 変更してはいけないコードはここまで。

	private int targetCompare(int i, int j, int k) {
		// suffixArrayを探索するときに使う比較関数。
		// 次のように定義せよ
		// suffix_i is a string in mySpace starting at i-th position.
		// target_i_k is a string in myTarget start at j-th postion ending k-th
		// position.
		// comparing suffix_i and target_j_k.
		// if the beginning of suffix_i matches target_i_k, it return 0.
		// The behavior is different from suffixCompare on this case.
		// if suffix_i > target_i_k it return 1;
		// if suffix_i < target_i_k it return -1;
		// It should be used to search the appropriate index of some suffix.
		// Example of search
		// suffix target
		// "o" > "i"
		// "o" < "z"
		// "o" = "o"
		// "o" < "oo"
		// "Ho" > "Hi"
		// "Ho" < "Hz"
		// "Ho" = "Ho"
		// "Ho" < "Ho " : "Ho " is not in the head of suffix "Ho"
		// "Ho" = "H" : "H" is in the head of suffix "Ho"

		if (mySpace.length - i < k - j) {
			return -1;
		}

		for (int idx = 0; idx < k - j; idx++) {
			if (i + idx < suffixArray.length) {
				if (mySpace[i + idx] < myTarget[idx + j]) {
					return -1;
				} else if (mySpace[i + idx] > myTarget[idx + j]) {
					return 1;
				}
			}
		}
		return 0;
	}

	private int subByteStartIndex(int start, int end) {
		// suffix arrayのなかで、目的の文字列の出現が始まる位置を求めるメソッド
		// 以下のように定義せよ。
		/*
		 * Example of suffix created from "Hi Ho Hi Ho" 0: Hi Ho 1: Ho 2: Ho Hi Ho 3:Hi
		 * Ho 4:Hi Ho Hi Ho 5:Ho 6:Ho Hi Ho 7:i Ho 8:i Ho Hi Ho 9:o A:o Hi Ho
		 */

		// It returns the index of the first suffix
		// which is equal or greater than target_start_end.
		// Assuming the suffix array is created from "Hi Ho Hi Ho",
		// if target_start_end is "Ho", it will return 5.
		// Assuming the suffix array is created from "Hi Ho Hi Ho",
		// if target_start_end is "Ho ", it will return 6.
		//
		// ここにコードを記述せよ。
		//

		if (targetCompare(suffixArray[0], start, end) == 0) {
			return 0;
		}

		int mid;
		int left = 0;
		int right = mySpace.length - 1;

		for (; right >= left;) {
			mid = (left + right) / 2;
			int res = targetCompare(suffixArray[mid], start, end);
			if (res == -1) {
				left = mid + 1;
			} else if (res == 1) {
				right = mid - 1;
			} else {
				if (targetCompare(suffixArray[mid - 1], start, end) == -1) {
					return mid;
				} else {
					right = mid - 1;
				}
			}
		}
		return -1;
	}

	private int subByteEndIndex(int start, int end) {
		// suffix arrayのなかで、目的の文字列の出現しなくなる場所を求めるメソッド
		// 以下のように定義せよ。
		/*
		 * Example of suffix created from "Hi Ho Hi Ho" 0: Hi Ho 1: Ho 2: Ho Hi Ho 3:Hi
		 * Ho 4:Hi Ho Hi Ho 5:Ho 6:Ho Hi Ho 7:i Ho 8:i Ho Hi Ho 9:o A:o Hi Ho
		 */
		// It returns the index of the first suffix
		// which is greater than target_start_end; (and not equal to target_start_end)
		// Assuming the suffix array is created from "Hi Ho Hi Ho",
		// if target_start_end is "Ho", it will return 7 for "Hi Ho Hi Ho".
		// Assuming the suffix array is created from "Hi Ho Hi Ho",
		// if target_start_end is"i", it will return 9 for "Hi Ho Hi Ho".
		//
		// ここにコードを記述せよ
		//
		int mid;
		int left = 0;
		int right = mySpace.length - 1;

		for (; right >= left;) {
			mid = (left + right) / 2;
			int res = targetCompare(suffixArray[mid], start, end);
			if (res == -1) {
				left = mid + 1;
			} else if (res == 1) {
				right = mid - 1;
			} else {
				if (mid == mySpace.length - 1 || targetCompare(suffixArray[mid + 1], start, end) == 1) {
					return mid + 1;
				} else {
					left = mid + 1;
				}
			}
		}
		return -1;
	}

	// Suffix Arrayを使ったプログラムのホワイトテストは、
	// privateなメソッドとフィールドをアクセスすることが必要なので、
	// クラスに属するstatic mainに書く方法もある。
	// static mainがあっても、呼びださなければよい。
	// 以下は、自由に変更して実験すること。
	// 注意：標準出力、エラー出力にメッセージを出すことは、
	// static mainからの実行のときだけに許される。
	// 外部からFrequencerを使うときにメッセージを出力してはならない。
	// 教員のテスト実行のときにメッセージがでると、仕様にない動作をするとみなし、
	// 減点の対象である。
	public static void main(String[] args) {
		Frequencer frequencerObject;
		try {
			frequencerObject = new Frequencer();
			frequencerObject.setSpace("Hi Ho Hi Ho".getBytes());
			frequencerObject.printSuffixArray(); // you may use this line for DEBUG
			/*
			 * Example from "Hi Ho Hi Ho" 0: Hi Ho 1: Ho 2: Ho Hi Ho 3:Hi Ho 4:Hi Ho Hi Ho
			 * 5:Ho 6:Ho Hi Ho 7:i Ho 8:i Ho Hi Ho 9:o A:o Hi Ho
			 */

			frequencerObject.setTarget("H".getBytes());
			//
			// **** Please write code to check subByteStartIndex, and subByteEndIndex
			//
			// int res = frequencerObject.subByteStartIndex(0, 1);
			// System.out.println(res);
			// res = frequencerObject.subByteEndIndex(0, 1);
			// System.out.println(res);

			int result = frequencerObject.frequency();
			System.out.print("Freq = " + result + " ");
			if (4 == result) {
				System.out.println("OK");
			} else {
				System.out.println("WRONG");
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
