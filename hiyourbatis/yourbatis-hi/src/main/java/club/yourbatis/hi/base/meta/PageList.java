package club.yourbatis.hi.base.meta;

import lombok.Data;
import java.util.List;

@Data
public class PageList<T>{
    private int count;
    List<T> list;
}