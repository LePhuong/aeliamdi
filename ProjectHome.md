AeliaMDI is a lightweight MDI Framework, branched off of AceMDI, that facilitates the creation of professional looking MDI applications.

When developing Swing Applications, JDesktopPane and JInternalFrame are provided for use with MDI applications. However, JInternalFrame has some pretty annoying quirks. For instance, when a JInternalFrame is maximized it only occupies the whole area of the desktop pane with its title bar as is. What a general user might expect is that the title bar should vanish and the minimize, maximize and close buttons should appear in the menubar of the main application frame.

Due to these shortcomings, many prefer not use JInternalFrame and instead opt for closeable tabs to represent their documents. However, in this approach they lose the facility to place documents side by side in a "restored" state to compare them.

AeliaMDI is designed to solve exactly these two problems. It manages your "views" as closable tabs when maximized and as internal frames when restored or minimized.

AeliaMDI is a fork of the last stable release of AceMDI available. Thanks go out to Pritam G. Barhate for creating such a great MDI library.

Migration from AceMDI to AeliaMDI is pretty straight-forward, but unfortunately there are certain small changes that prevent it from being a drop-in replacement.