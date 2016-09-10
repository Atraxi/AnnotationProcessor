package atraxi.annotation;

import atraxi.annotation.annotations.Serialize;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;

/**
 * Created by Atraxi on 8/09/2016.
 */
@SupportedAnnotationTypes("atraxi.annotation.annotations.Serialize")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class SerializationProcessor extends AbstractProcessor
{
    private String template;

    public SerializationProcessor()
    {
        super();
        try(BufferedReader reader = new BufferedReader(new FileReader(new File("/resources/SerializerTemplate.java"))))
        {
            String line = reader.readLine();
            StringBuilder stringBuilder = new StringBuilder();
            while(line != null)
            {
                stringBuilder.append(line);
                line = reader.readLine();
            }
            template = stringBuilder.toString();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        for(TypeElement typeElement : annotations)
        for(Element element : roundEnv.getElementsAnnotatedWith(typeElement))
        {
            Serialize serialize = element.getAnnotation(Serialize.class);
            String message = "annotation found in " + element.getSimpleName() + " with serialization visibility " + serialize.value();
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, message);

            if (element.getKind() == ElementKind.CLASS)
            {
                TypeElement classElement = (TypeElement) element;
                PackageElement packageElement =
                        (PackageElement) classElement.getEnclosingElement();

                String className = classElement.getQualifiedName() + "GeneratedSerializer";

                try(BufferedWriter writer = new BufferedWriter(processingEnv.getFiler().createSourceFile(className).openWriter()))
                {
                    writer.append(template.replace("$Package", packageElement.getQualifiedName())
                                          .replace("$ClassName", className));
                    writer.close();
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }
}
