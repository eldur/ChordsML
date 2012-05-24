package info.chordsml;


import java.io.File;
import java.util.List;

public interface LaTexStyle {

  List<File> getMisc();
  File getTexHeader();
  File getTexFooter();
  Transformator getSongTransformator();
  
}
