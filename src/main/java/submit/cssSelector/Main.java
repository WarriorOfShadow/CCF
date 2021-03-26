package submit;

import java.util.*;

class HtmlElement {
    int code;
    List<HtmlElement> subElements = new LinkedList<HtmlElement>();
    String id = null;
    String name;
    HtmlElement parent = null;
    public HtmlElement(String name){
        this.name = name;
    }

    public void addSubElem(HtmlElement element){
        this.subElements.add(element);
    }

    public List<HtmlElement> getSubElements() {
        return subElements;
    }

    public void setSubElements(List<HtmlElement> subElements) {
        this.subElements = subElements;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HtmlElement getParent() {
        return parent;
    }

    public void setParent(HtmlElement parent) {
        this.parent = parent;
    }

    public void setCode(int code) {
        this.code = code;
    }
}

class CSSSelector {
    private String[] selectorCmd;
    private List<HtmlElement> selectedElem;

    public CSSSelector(String CSSStr) {
        selectorCmd = CSSStr.split(" ");
    }

    private void selectElemByName(String elemName, HtmlElement element) {
        if (element.getName().equals(elemName)) {
            selectedElem.add(element);
        }
        for (HtmlElement subElem : element.subElements) {
            selectElemByName(elemName, subElem);
        }
    }

    private void selectElemById(String id, HtmlElement element) {
        if (element.id != null && element.getId().equals(id)) {
            selectedElem.add(element);
        }
        for (HtmlElement subElem : element.subElements) {
            selectElemById(id, subElem);
        }
    }

    private boolean isId(String cmd) {
        return cmd.charAt(0) == '#';
    }

    private boolean multilayerJudge(HtmlElement element){
        HtmlElement currentElem = element.parent;
        for(int i=selectorCmd.length-2;i>=0;i--){
            while (currentElem!=null){
                if (isId(selectorCmd[i])){
                    if (currentElem.id != null && currentElem.getId().equals(selectorCmd[i].substring(1))){
                        break;
                    }
                }
                else {
                    selectorCmd[i] = selectorCmd[i].toLowerCase();
                    if (currentElem.getName().equals(selectorCmd[i])){
                        break;
                    }
                }
                currentElem = currentElem.parent;
            }
            if (currentElem == null){
                return false;
            }
            currentElem = currentElem.parent;
        }
        return true;
    }

    public List<HtmlElement> getSelectedElem(HtmlElement html) {
        selectedElem = new LinkedList<HtmlElement>();
        String lastCmd = selectorCmd[selectorCmd.length-1];
        if (lastCmd.equals(""))
            return selectedElem;
        if (isId(lastCmd)){
            selectElemById(lastCmd.substring(1), html);
        }else {
            lastCmd = lastCmd.toLowerCase();
            selectElemByName(lastCmd, html);
        }
        selectedElem.sort(new Comparator<HtmlElement>() {
            public int compare(HtmlElement o1, HtmlElement o2) {
                return o1.code - o2.code;
            }
        });
        Iterator<HtmlElement> it = selectedElem.iterator();
        while (it.hasNext()){
            boolean ok = multilayerJudge(it.next());
            if (!ok){
                it.remove();
            }
        }
        return selectedElem;
    }
}

public class Main {
    static class InRet {
        int cnt;
        String name;

        public InRet(int cnt, String name) {
            this.cnt = cnt;
            this.name = name;
        }
    }

    static public Main.InRet getIndentation(String elemStr) {
        int cnt = 0;
        for (int i = 0; i < elemStr.length(); i++) {
            if (elemStr.charAt(i) == '.') {
                cnt++;
            } else {
                break;
            }
        }
        elemStr = elemStr.substring(cnt);
        return new Main.InRet(cnt, elemStr);
    }
    public static void main(String[] args) {
        Map<Integer, HtmlElement> indentationMap = new HashMap<Integer, HtmlElement>();
        Scanner s = new Scanner(System.in);
        int n, m;
        n = s.nextInt();
        m = s.nextInt();
        s.nextLine();
        for (int i = 0; i < n; i++) {
            String line = s.nextLine();
            String[] parts = line.split(" ");
            InRet ret = getIndentation(parts[0]);
            int indentation = ret.cnt;
            String elemName = ret.name;
            HtmlElement newElement = new HtmlElement(elemName.toLowerCase());
            newElement.setCode(i + 1);
            indentationMap.put(indentation, newElement);
            if (indentationMap.containsKey(indentation - 2)) {
                indentationMap.get(indentation - 2).addSubElem(newElement);
                newElement.setParent(indentationMap.get(indentation - 2));
            }

            String id = null;
            if (parts.length == 2) {
                id = parts[1].substring(1);
            }
            newElement.setId(id);

        }
        HtmlElement html = indentationMap.get(0);

        for (int i = 0; i < m; i++) {
            String CSSStr = s.nextLine();
            CSSSelector cssSelector = new CSSSelector(CSSStr);
            List<HtmlElement> selectedElem = cssSelector.getSelectedElem(html);

            System.out.print(selectedElem.size());
            for (HtmlElement element : selectedElem) {
                System.out.print(" ");
                System.out.print(element.code);
            }
            System.out.println();
        }
    }
}
