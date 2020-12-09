declare module 'react-native-phpfox-mobile' {
  interface CompressOptions {
    path: string,
    mime: string,
    limit: number,
    forceCompress?: boolean
  }

  interface CompressResult {
    limit: number,
    path: string,
    filesize: number,
    quality: number,
    original_path: string,
    original_filesize: number,
    compress_success: boolean,
    isHeic: boolean,
    mime: string | null,
    filename: string | null
  }

  export const ImageCompresser: {
    compress(options: CompressOptions): Promise<CompressResult>
  }
}
