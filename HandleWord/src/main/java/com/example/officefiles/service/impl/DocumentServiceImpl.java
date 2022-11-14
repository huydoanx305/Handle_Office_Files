package com.example.officefiles.service.impl;

import com.example.officefiles.common.CommonConstant;
import com.example.officefiles.common.TextNodesXPath;
import com.example.officefiles.entity.Document;
import com.example.officefiles.exception.NotFoundException;
import com.example.officefiles.repository.DocumentRepository;
import com.example.officefiles.service.DocumentService;
import com.example.officefiles.service.SegmentService;
import com.example.officefiles.utils.FileUtil;
import org.docx4j.XmlUtils;
import org.docx4j.docProps.extended.Properties;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.DocPropsExtendedPart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.P;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        List<Object> jaxbNodes = mainDocumentPart.getJAXBNodesViaXPath(TextNodesXPath.P.getValue(), true);
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
    public Map<String, Object> previewDocument(Long documentId) throws Exception {
        Optional<Document> document = documentRepository.findById(documentId);
        if(document.isEmpty()) {
            throw new NotFoundException("Not found");
        }
        File file = FileUtil.getFileByPath(document.get().getPath());

        WordprocessingMLPackage newDoc = WordprocessingMLPackage.createPackage(); // Tạo docx định dạng OpenXML
        MainDocumentPart createMainDocumentPart = newDoc.getMainDocumentPart(); // get file document.xml

        // loading existing document
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(file); // Chuyển file sang docx định dạng OpenXML
        MainDocumentPart mainDocumentPart = wordMLPackage.getMainDocumentPart(); // get file document.xml

        int nowPage = 0;
        List<Object> jaxbNodesP = mainDocumentPart.getJAXBNodesViaXPath(TextNodesXPath.P.getValue(), true);
        for (Object paragraph : jaxbNodesP) {
            P p = ((P) XmlUtils.unwrap(paragraph));
            for (Object run : p.getContent()) {
                if(!mainDocumentPart.getJAXBNodesViaXPath(TextNodesXPath.LRPB.getValue(), run, true).isEmpty()) {
                    nowPage++;
                }
            }
            if (nowPage > 1) {
                break;
            }
            createMainDocumentPart.addObject(paragraph); // add các p vào doc
        }
        newDoc.save(FileUtil.saveFileToPreview(FileUtil.setNamePreview(file.getName()))); // lưu file doc mới

        Map<String, Object> map = new HashMap<>();
        map.put("filePath", document.get().getPath());
        map.put("previewPath", FileUtil.setNamePreview(file.getName()));
        map.put("page", nowPage);
        return map;
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
