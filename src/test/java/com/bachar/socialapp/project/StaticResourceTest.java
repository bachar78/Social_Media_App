package com.bachar.socialapp.project;

import com.bachar.socialapp.project.configuration.AppConfiguration;
import com.bachar.socialapp.project.configuration.WebConfiguration;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class StaticResourceTest {

    @Autowired
    AppConfiguration appConfiguration;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void checkStaticFolder_WhenAppIsInitialized_uploadFolderMustExist() {
        File uploadFolder = new File(appConfiguration.getUploadPath());
        boolean uploadFolderExist = uploadFolder.exists() && uploadFolder.isDirectory();
        assertThat(uploadFolderExist).isTrue();
    }

    @Test
    public void checkStaticFolder_whenAppIsInitialized_profileImageSubFolderMustExist() {
        String profileImageFolderPath = appConfiguration.getFullProfileImagesPath();
        File profileImageFolder = new File(profileImageFolderPath);
        boolean profileImageFolderExists = profileImageFolder.exists() && profileImageFolder.isDirectory();
        assertThat(profileImageFolderExists).isTrue();
    }

    @Test
    public void checkStaticFolder_whenAppIsInitialized_attachmentsSubFolderMustExist() {
        String attachmentsFolderPath = appConfiguration.getFullAttachmentPath();
        File attachmentsFolder = new File(attachmentsFolderPath);
        boolean profileAttachmentsFolderExists = attachmentsFolder.exists() && attachmentsFolder.isDirectory();
        assertThat(profileAttachmentsFolderExists).isTrue();
    }

//    @Test
//    public void getStaticFile_whenImageExistInProfileUploadFolder_receiveOk() throws Exception {
//        String fileName = "profile-picture.png";
//        File source = new ClassPathResource("profile.png").getFile();
//
//        File target = new File(appConfiguration.getFullProfileImagesPath() + "/" + fileName);
//        FileUtils.copyFile(source, target);
//
//        mockMvc.perform(get("/images/"+appConfiguration.getProfileImagesFolder()+"/"+fileName)).andExpect(status().isOk());
//    }

        @AfterEach
    public void cleanDirectory() throws IOException {
        FileUtils.cleanDirectory(new File(appConfiguration.getFullProfileImagesPath()));
        FileUtils.cleanDirectory(new File(appConfiguration.getFullAttachmentPath()));
    }



}
