package concord;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class TextConcord {

	Map<String, WordData> concord = new HashMap<String, WordData>();
	/**
	 * @param args
	 *  Takes a directory parameter and outputs a concordance to concord.<timestamp>.out
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		File dir = new File(args[0]);
		if (!dir.isDirectory()) {
			System.err.println("Error: " + args[0] + " is not a directory");
			System.exit(1);
		}	
		TextConcord tc = new TextConcord();
		tc.doConcordance(dir);
		System.out.println("Did concordance");
	}

	private void doConcordance(File dir) throws IOException {
		File[] files = dir.listFiles();
		for (int i=0; i<files.length; i++) {
			parse(files[i]);
		}
		output();
	}

	private void output() throws IOException {
		Collection<WordData> words = concord.values();
		SortedSet<WordData> sorted = new TreeSet<WordData>();
		for (java.util.Iterator<WordData> it = words.iterator(); it.hasNext();)
			sorted.add(it.next());
		String outputFileName = "concord." + System.currentTimeMillis() + ".out";
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFileName),"UTF8"));
		for (Iterator<WordData> it = sorted.iterator(); it.hasNext();) {
			WordData data = it.next();
			byte[] utf8Bytes = data.word.getBytes("UTF8");
			String utf8String = new String(utf8Bytes, "UTF8");
			bw.write(utf8String +  "\n"); 
		}
		bw.close();
	}

	private void parse(File file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		while (true) {
			line = br.readLine();
			if (line == null) break;
	
			String[] words = line.split("[\\s,;.\"()\\[\\]:?!<>{}*]");
			
			for (int i=0; i<words.length; i++)
			{
				addWord(words[i], file.getName());
			}
		}
	}

	private void addWord(String word, String fileName) {
		if (word.equals("")) return;
		String lowerCaseWord = word.toLowerCase();
		if (concord.containsKey(lowerCaseWord)) {
			WordData data = concord.get(lowerCaseWord);
			data.count++;
			data.fileNames.add(fileName);
		/*} else if (concord.containsKey(word.toLowerCase())) {
			WordData data = concord.get(word.toLowerCase());
			data.count++;
			data.fileNames.add(fileName); */ //Moving to using all lowercase. I'll recapitalize by hand if needed
		} else {
			WordData data = new WordData();
			data.count=1;
			data.word = lowerCaseWord;
			data.fileNames = new HashSet<String>();
			data.fileNames.add(fileName);
			concord.put(lowerCaseWord, data);
		}
	}

	class WordData implements Comparable {

		String word;
		int count;
		Set<String> fileNames;
		
		//backwards to get descending order
		public int compareTo(Object o) {
			WordData other = (WordData) o;
			if (this.count < other.count) {
				return 1;
			} else if (other.count == this.count) {
				return this.word.compareTo(other.word);
			} else { 
				return -1;
			}
		}
	}
	
}
