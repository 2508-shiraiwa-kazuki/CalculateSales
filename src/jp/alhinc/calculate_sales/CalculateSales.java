package jp.alhinc.calculate_sales;

import java.io.BufferedReader;
//BufferedWriterをimportした
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
//FileWriterをimportした
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalculateSales {
		//以下定数
	// 支店定義ファイル名
	private static final String FILE_NAME_BRANCH_LST = "branch.lst";

	// 支店別集計ファイル名
	private static final String FILE_NAME_BRANCH_OUT = "branch.out";

	// エラーメッセージ
	private static final String UNKNOWN_ERROR = "予期せぬエラーが発生しました";
	private static final String FILE_NOT_EXIST = "支店定義ファイルが存在しません";
	private static final String FILE_INVALID_FORMAT = "支店定義ファイルのフォーマットが不正です";

	/**
	 * メインメソッド
	 *
	 * @param コマンドライン引数
	 */
	public static void main(String[] args) {
		// 支店コードと支店名を保持するMap
		Map<String, String> branchNames = new HashMap<>();
		// 支店コードと売上金額を保持するMap
		Map<String, Long> branchSales = new HashMap<>();

		// 支店定義ファイル読み込み処理
		if(!readFile(args[0], FILE_NAME_BRANCH_LST, branchNames, branchSales)) {
			return;
		}

		// ※ここから集計処理を作成してください。(処理内容2-1、2-2)
		File[] files = new File("C:\\Users\\trainee1384\\Desktop\\売り上げ集計課題").listFiles();
		List<File> rcdFiles = new ArrayList<>();

		//ファイル名のチェック機能（「数字8桁.rcd」になっているか）
		for(int i = 0; i < files.length; i++) {
			if(files[i].getName().matches("^[0-9]{8}" + ".rcd")) {
				rcdFiles.add(files[i]);
			}
		}

		//BufferedReaderの初期化
		BufferedReader br = null;

		//rcdFilesの要素の読込機能
		for(int i = 0; i < rcdFiles.size(); i++) {

			try {
				FileReader fr = new FileReader(rcdFiles.get(i));	//rcdFilesはリストなので.get()
				br = new BufferedReader(fr);	//初期化時にBufferedReader brとしているのでここではbrのみ

				List<String> storeSale = new ArrayList<>();
				String line;
				while((line = br.readLine()) != null) {		//rcdFilesのすべての行をreadLineするまで繰り返し
					storeSale.add(line);
					//読み込んだ内容をListに追加していく
					}
				//String型の売上金額をLong型に変換
				long fileSale = Long.parseLong(storeSale.get(1));
				//branchSalesに保持されている売上金額に合計する
				Long saleAmount = branchSales.get(storeSale.get(0)) + fileSale;
				branchSales.put(storeSale.get(0),saleAmount);

			}catch(IOException e) {
				System.out.println("UNKNOWN ERROR");
				return;

			}finally {
				if(br != null) {
					try {
						br.close();
					}catch(IOException e) {
						System.out.println("UNKNOWN ERROR");
						return;
					}
				}
			}
		}


		// 支店別集計ファイル書き込み処理
		if(!writeFile(args[0], FILE_NAME_BRANCH_OUT, branchNames, branchSales)) {
			return;
		}

	}

	/**
	 * 支店定義ファイル読み込み処理
	 *
	 * @param フォルダパス
	 * @param ファイル名
	 * @param 支店コードと支店名を保持するMap
	 * @param 支店コードと売上金額を保持するMap
	 * @return 読み込み可否
	 */
	private static boolean readFile(String path, String fileName, Map<String, String> branchNames, Map<String, Long> branchSales) {
		BufferedReader br = null;

		try {
			File file = new File(path, fileName);
			FileReader fr = new FileReader(file);
			br = new BufferedReader(fr);

			String line;
			// 一行ずつ読み込む
			while((line = br.readLine()) != null) {
				// ※ここの読み込み処理を変更してください。(処理内容1-2)
				String[] items = line.split(",");

				branchNames.put(items[0],items[1]);
				branchSales.put(items[0],0L);

				System.out.println(line);
			}

		} catch(IOException e) {
			System.out.println(UNKNOWN_ERROR);
			return false;
		} finally {
			// ファイルを開いている場合
			if(br != null) {
				try {
					// ファイルを閉じる
					br.close();
				} catch(IOException e) {
					System.out.println(UNKNOWN_ERROR);
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 支店別集計ファイル書き込み処理
	 *
	 * @param フォルダパス
	 * @param ファイル名
	 * @param 支店コードと支店名を保持するMap
	 * @param 支店コードと売上金額を保持するMap
	 * @return 書き込み可否
	 */
	private static boolean writeFile(String path, String fileName, Map<String, String> branchNames, Map<String, Long> branchSales) {
		// ※ここに書き込み処理を作成してください。(処理内容3-1)
		BufferedWriter bw = null;
		//keyの数の分だけ以下の処理を繰り返す
		try {
			File file = new File(path,fileName);
			FileWriter fw = new FileWriter(file);
			bw = new BufferedWriter(fw);

			for(String key : branchNames.keySet()) {
				//Long型の売上金額をString型に変換
				//String p = String.valueOf(変化させる数値);
				String saleAmount = String.valueOf(branchSales.get(key));
				//実際にファイルに出力する形式を設定
				//支店コード、支店名、合計金額をカンマ区切りで出力、支店ごとに改行
				bw.write(key + "," + branchNames.get(key) + "," + saleAmount);
				//改行
				bw.newLine();
			}
			//BRANCH_OUTファイルにすべて出力するので、for文の後に書き込みを停止
			bw.close();
		}catch(IOException e) {
				System.out.println(UNKNOWN_ERROR);
				return false;
		}finally {
			if(bw != null) {
				try {
					bw.close();
				}catch(IOException e) {
					System.out.println(UNKNOWN_ERROR);
					return false;
				}
			}
		}
		return true;
	}

}
