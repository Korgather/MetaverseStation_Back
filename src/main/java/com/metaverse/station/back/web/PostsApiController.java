package com.metaverse.station.back.web;

import com.metaverse.station.back.service.PostsService;
import com.metaverse.station.back.utils.S3Uploader;
import com.metaverse.station.back.web.dto.PostsResponseDto;
import com.metaverse.station.back.web.dto.PostsSaveRequestDto;
import com.metaverse.station.back.web.dto.PostsSaveRequestResponseDto;
import com.metaverse.station.back.web.dto.PostsUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class PostsApiController {

    private final PostsService postsService;
    private final S3Uploader s3Uploader;


//    @GetMapping("/api/v1/posts")
//    public List<PostsResponseDto> findAll(@PageableDefault(size = 8) Pageable pageable) {
//        return postsService.findAll(pageable);
//    }

    @GetMapping("/api/v1/posts")
    public Page<PostsResponseDto> findAll(@RequestParam String keyword, @PageableDefault(size = 8,sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        if (keyword == null) {
            return postsService.findAll(pageable);
        }
        else {
            return postsService.findByContentOrTitle(keyword, pageable);
        }
    }

//    @GetMapping("/api/v1/posts")
//    public Page<PostsResponseDto> findByContentAndTitle(@RequestParam String keyword, @PageableDefault(size = 8,sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
//        return postsService.findByContentAndTitle(keyword, pageable);
//    }

    @PostMapping("/api/v1/posts")
    public PostsSaveRequestResponseDto save(@RequestBody PostsSaveRequestDto requestDto) {

        return postsService.save(requestDto);
    }

    @GetMapping("/api/v1/posts/{id}")
    public PostsResponseDto findById(@PathVariable Long id) {

        return postsService.findById(id);
    }

    @PutMapping("/api/v1/posts/{id}")
    public Long update(@PathVariable Long id, @RequestBody PostsUpdateRequestDto requestDto) {
        return postsService.update(id, requestDto);
    }

    @DeleteMapping("/api/v1/posts/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        return postsService.deletePost(id);
    }

    @GetMapping("/api/v1/posts/view/{id}")
    public String viewCountUp(@PathVariable Long id) {

        return postsService.updateView(id);
    }



    @PostMapping("/api/v1/upload")
    @ResponseBody
    public List<String> upload(@RequestParam("data") List<MultipartFile> multipartFile) throws IOException {
        return s3Uploader.upload(multipartFile, "static");
    }


    @GetMapping("/api/v1/posts/likepost/{id}")
    public Page<PostsResponseDto> getLikePost(@PathVariable Long id, @PageableDefault(size = 8) Pageable pageable) {

        return postsService.findByLikeUserId(id,pageable);
    }

    @GetMapping("/api/v1/posts/userid/{id}")
    public Page<PostsResponseDto> getUserPost(@PathVariable Long id, @PageableDefault(size = 8) Pageable pageable) {

        return postsService.findByUserId(id,pageable);
    }


}
