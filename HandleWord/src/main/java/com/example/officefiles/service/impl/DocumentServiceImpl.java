package com.example.officefiles.service.impl;

import com.example.officefiles.common.CommonConstant;
import com.example.officefiles.entity.Document;
import com.example.officefiles.exception.NotFoundException;
import com.example.officefiles.repository.DocumentRepository;
import com.example.officefiles.service.DocumentService;
import com.example.officefiles.service.SegmentService;
import com.example.officefiles.utils.FileUtil;
import org.docx4j.TextUtils;
import org.docx4j.XmlUtils;
import org.docx4j.convert.out.common.preprocess.PageNumberInformation;
import org.docx4j.convert.out.common.preprocess.PageNumberInformationCollector;
import org.docx4j.docProps.extended.Properties;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.DocPropsExtendedPart;
import org.docx4j.openpackaging.parts.WordprocessingML.FooterPart;
import org.docx4j.openpackaging.parts.WordprocessingML.HeaderPart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.openpackaging.parts.WordprocessingML.StyleDefinitionsPart;
import org.docx4j.openpackaging.parts.relationships.Namespaces;
import org.docx4j.openpackaging.parts.relationships.RelationshipsPart;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.yaml.snakeyaml.introspector.PropertyUtils;

import javax.transaction.Transactional;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;

    private final SegmentService segmentService;

    public DocumentServiceImpl(DocumentRepository documentRepository, SegmentService segmentService) {
        this.documentRepository = documentRepository;
        this.segmentService = segmentService;
    }

    @Override
    public List<Document> getAllDocument() {
        return documentRepository.findAll();
    }

    @Override
    @Transactional
    public Document save(MultipartFile file) throws Exception {
        if(file.isEmpty()) {
            throw new NotFoundException("Not found");
        }
        File doc = FileUtil.convertMultipartToFile(file);
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(doc); // Chuyển file sang docx định dạng OpenXML
        MainDocumentPart mainDocumentPart = wordMLPackage.getMainDocumentPart(); // get file document.xml

        //w:p - 1 đoạn văn (khi xuống dòng)
        //w:r - 1 câu (có style giống nhau)
        String textNodesXPath = "//w:p";
        List<Object> jaxbNodes = mainDocumentPart.getJAXBNodesViaXPath(textNodesXPath, true);
        if(jaxbNodes.isEmpty()) {
            throw new NotFoundException("Empty file");
        }

        DocPropsExtendedPart docPropsExtendedPart =  wordMLPackage.getDocPropsExtendedPart(); // get file app trong DocProps
        Properties extendedProps = docPropsExtendedPart.getContents(); // get properties trong file app
        final Integer pages = extendedProps.getPages(); // get page

        Document document = new Document();
        document.setFileName(doc.getName());
        document.setExt(FileUtil.getTypeFile(doc.getName()));
        document.setPath(CommonConstant.UPLOAD + "/" + doc.getName());
        document.setPage(pages);
        documentRepository.save(document);
//        segmentService.saveSegment(jaxbNodes, document);
        return document;
    }

    @Override
    public Document previewDocument(Long documentId) throws Exception {
        Optional<Document> document = documentRepository.findById(documentId);
        if(document.isEmpty()) {
            throw new NotFoundException("Not found");
        }
        File file = FileUtil.getFileByPath(document.get().getPath());

        // loading existing document
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(file); // Chuyển file sang docx định dạng OpenXML
        MainDocumentPart mainDocumentPart = wordMLPackage.getMainDocumentPart(); // get file document.xml

        int nowPage = 0;
        String textNodesXPathP = "//w:p";
        List<Object> jaxbNodesP = mainDocumentPart.getJAXBNodesViaXPath(textNodesXPathP, true);
        for (Object p : jaxbNodesP) {
            String textNodesXPathR = "//w:r";
            List<Object> jaxbNodesR = mainDocumentPart.getJAXBNodesViaXPath(textNodesXPathR, p, true);
            for (Object r : jaxbNodesR) {
                String textNodesXPathPB = "//w:lastRenderedPageBreak";
                List<Object> jaxbNodesPB = mainDocumentPart.getJAXBNodesViaXPath(textNodesXPathPB, r, true);
                for(Object pb : jaxbNodesPB) {
                    if(pb != null) {
                        nowPage++;
                    }
                }
            }
            if (nowPage > 1) {
                break;
            }
            WordprocessingMLPackage doc = WordprocessingMLPackage.createPackage(); // Tạo docx định dạng OpenXML
            MainDocumentPart createMainDocumentPart = doc.getMainDocumentPart(); // get file document.xml
            createMainDocumentPart.addObject(p); // add các p vào doc
            doc.save(new File("result.docx")); // lưu file mới
        }
        return null;
    }

    // Cách 1 convert XML to JSON (củ chuối -> bỏ :v)
//        JSONObject objectDocument = null;
//        JSONObject objectApp = null;
//        try {
//            objectDocument = new JSONObject(FileConverter.convertXMLtoJsonString(
//                    FileConverter.zipperAndGetDocumentFileWord(FileConverter.convertMultipartToFile(file))
//            ));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        if(objectDocument !=  null) {
//            JSONArray array = new JSONArray(objectDocument.getJSONObject("w:document").getJSONObject("w:body").get("w:p").toString());
//            for(int i=0; i < array.length(); i++)
//            {
//                JSONObject obj = array.getJSONObject(i);
//                System.out.println(obj);
//            }
//        }
}
