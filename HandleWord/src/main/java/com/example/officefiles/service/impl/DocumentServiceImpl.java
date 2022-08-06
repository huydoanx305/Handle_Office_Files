package com.example.officefiles.service.impl;

import com.example.officefiles.entity.Document;
import com.example.officefiles.exception.NotFoundException;
import com.example.officefiles.repository.DocumentRepository;
import com.example.officefiles.service.DocumentService;
import com.example.officefiles.service.SegmentService;
import com.example.officefiles.utils.FileConverter;
import org.docx4j.docProps.extended.Properties;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.DocPropsExtendedPart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.util.List;

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
        File doc = FileConverter.convertMultipartToFile(file);
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(doc);
        MainDocumentPart mainDocumentPart = wordMLPackage.getMainDocumentPart();
        String textNodesXPath = "//w:p";
        List<Object> jaxbNodes = mainDocumentPart.getJAXBNodesViaXPath(textNodesXPath, true);

        DocPropsExtendedPart docPropsExtendedPart =  wordMLPackage.getDocPropsExtendedPart();
        Properties extendedProps = docPropsExtendedPart.getContents();
        final Integer pages = extendedProps.getPages();

        if(jaxbNodes.isEmpty()) {
            throw new NotFoundException("Empty file");
        }
        Document document = new Document();
        document.setFileName(doc.getName());
        document.setExt(doc.getName().substring(doc.getName().lastIndexOf(".") + 1));
        document.setPath(doc.getPath());
        document.setPage(pages);
        documentRepository.save(document);
        segmentService.saveSegment(jaxbNodes, document);
        return document;
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
