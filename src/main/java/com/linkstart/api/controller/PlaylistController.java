package com.linkstart.api.controller;

import com.linkstart.api.model.dto.PlaylistDto;
import com.linkstart.api.service.PlaylistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/playlists")
public class PlaylistController {

    private final PlaylistService playlistService;

    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    @GetMapping
    public ResponseEntity<List<PlaylistDto>> getPlaylists() {
        return ResponseEntity.ok(playlistService.getPlaylists());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlaylistDto> getPlaylistById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(playlistService.getPlaylistById(id));
    }

    @PostMapping
    public ResponseEntity<PlaylistDto> createPlaylist(@RequestBody PlaylistDto playlistDto, @RequestParam String memberId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(playlistService.createPlaylist(playlistDto, memberId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlaylistDto> updatePlaylist(@PathVariable("id") Integer id, @RequestBody PlaylistDto playlistDto) {
        return ResponseEntity.ok(playlistService.updatePlaylist(id, playlistDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deletePlaylist(@PathVariable("id") Integer id) {
        playlistService.deletePlaylist(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<PlaylistDto>> searchPlaylists(
            @RequestParam String filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int size,
            @RequestParam(defaultValue = "username") String orderBy,
            @RequestParam(defaultValue = "true") Boolean ascending) {
        return ResponseEntity.ok(playlistService.searchPlaylists(filter, page, size, orderBy, ascending));
    }
}
